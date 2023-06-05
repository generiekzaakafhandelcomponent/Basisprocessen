package com.ritense.valtimo.implementation.service;

import com.ritense.document.domain.impl.JsonSchemaDocument;
import com.ritense.document.domain.impl.JsonSchemaDocumentDefinitionRole;
import com.ritense.document.domain.search.AdvancedSearchRequest;
import com.ritense.document.domain.search.SearchOperator;
import com.ritense.document.domain.search.SearchRequestValidator;
import com.ritense.document.service.SearchFieldService;
import com.ritense.document.service.impl.JsonSchemaDocumentSearchService;
import com.ritense.valtimo.contract.authentication.UserManagementService;
import com.ritense.valtimo.contract.database.QueryDialectHelper;
import com.ritense.valtimo.contract.utils.SecurityUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.query.criteria.internal.OrderImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import javax.transaction.Transactional;
import java.time.*;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

// This class can be removed when switching to Valtimo Major 10
@Transactional
public class CustomDocumentSearchService extends JsonSchemaDocumentSearchService {

    private static final String DOCUMENT_DEFINITION_ID = "documentDefinitionId";
    private static final String DOCUMENT_DEFINITION_NAME = "documentDefinitionName";
    private static final String ID = "id";
    private static final String ROLE = "role";
    private static final String NAME = "name";
    private static final String CONTENT = "content";
    private static final String DOC_PREFIX = "doc:";
    private static final String CASE_PREFIX = "case:";

    private final EntityManager entityManager;
    private final QueryDialectHelper queryDialectHelper;

    public CustomDocumentSearchService(
            EntityManager entityManager,
            QueryDialectHelper queryDialectHelper,
            SearchFieldService searchFieldService,
            UserManagementService userManagementService
    ) {
        super(entityManager, queryDialectHelper, searchFieldService, userManagementService);
        this.entityManager = entityManager;
        this.queryDialectHelper = queryDialectHelper;
    }

    @Override
    public Page<JsonSchemaDocument> search(String documentDefinitionName, AdvancedSearchRequest advancedSearchRequest, Pageable pageable) {
        SearchRequestValidator.validate(advancedSearchRequest);
        return search(
            (cb, query, documentRoot) -> buildQueryWhere(documentDefinitionName, advancedSearchRequest, cb, query, documentRoot),
            pageable
        );
    }

    private Page<JsonSchemaDocument> search(QueryWhereBuilder queryWhereBuilder, Pageable pageable) {
        final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        final CriteriaQuery<JsonSchemaDocument> query = cb.createQuery(JsonSchemaDocument.class);
        final Root<JsonSchemaDocument> selectRoot = query.from(JsonSchemaDocument.class);

        query.select(selectRoot);
        queryWhereBuilder.apply(cb, query, selectRoot);
        query.orderBy(getOrderBy(cb, selectRoot, pageable.getSort()));

        final TypedQuery<JsonSchemaDocument> typedQuery = entityManager.createQuery(query);

        if (pageable.isPaged()) {
            typedQuery
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize());
        }

        final CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<JsonSchemaDocument> countRoot = countQuery.from(JsonSchemaDocument.class);
        countQuery.select(cb.count(countRoot));
        queryWhereBuilder.apply(cb, countQuery, selectRoot);

        return new PageImpl<>(typedQuery.getResultList(), pageable, entityManager.createQuery(countQuery).getSingleResult());
    }


    private void buildQueryWhere(
        String documentDefinitionName,
        AdvancedSearchRequest searchRequest,
        CriteriaBuilder cb,
        CriteriaQuery<?> query,
        Root<JsonSchemaDocument> documentRoot
    ) {
        final List<Predicate> predicates = new ArrayList<>();

        if (!StringUtils.isEmpty(documentDefinitionName)) {
            predicates.add(cb.equal(documentRoot.get(DOCUMENT_DEFINITION_ID).get(NAME), documentDefinitionName));
        }

        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            addUserRolePredicate(query, documentRoot, predicates);
        }

        if (searchRequest.getOtherFilters() != null && !searchRequest.getOtherFilters().isEmpty()) {
            predicates.add(getOtherFilersPredicate(cb, documentRoot, searchRequest));
        }
        query.where(predicates.toArray(new Predicate[0]));
    }

    private Predicate getOtherFilersPredicate(
        CriteriaBuilder cb,
        Root<JsonSchemaDocument> root,
        AdvancedSearchRequest searchRequest
    ) {
        var jsonPredicates = searchRequest.getOtherFilters().stream()
            .map(currentCriteria -> buildQueryForSearchCriteria(cb, root, currentCriteria))
            .collect(toList())
            .toArray(Predicate[]::new);

        if (searchRequest.getSearchOperator() == SearchOperator.AND) {
            return cb.and(jsonPredicates);
        } else {
            return cb.or(jsonPredicates);
        }
    }

    private void addUserRolePredicate(CriteriaQuery<?> query,
                                      Root<JsonSchemaDocument> documentRoot,
                                      List<Predicate> predicates) {
        List<String> roles = SecurityUtils.getCurrentUserRoles();

        Subquery<String> sub = query.subquery(String.class);
        Root<JsonSchemaDocumentDefinitionRole> subRoot = sub.from(JsonSchemaDocumentDefinitionRole.class);
        sub.select(subRoot.get(ID).get(DOCUMENT_DEFINITION_NAME));
        sub.where(subRoot.get(ID).get(ROLE).in(roles));

        predicates.add(
            documentRoot.get(DOCUMENT_DEFINITION_ID).get(NAME).in(sub)
        );
    }

    private <T extends Comparable<? super T>> Predicate buildQueryForSearchCriteria(
        CriteriaBuilder cb,
        Root<JsonSchemaDocument> root,
        AdvancedSearchRequest.OtherFilter searchCriteria
    ) {
        Expression<T> value;
        if (searchCriteria.getPath().startsWith(DOC_PREFIX)) {
            value = getValueExpressionForDocPrefix(cb, root, searchCriteria);
        } else if (searchCriteria.getPath().startsWith(CASE_PREFIX)) {
            value = getValueExpressionForCasePrefix(root, searchCriteria);
        } else {
            throw new IllegalArgumentException("Search path doesn't start with known prefix: '" + searchCriteria.getPath() + "'");
        }

        var rangeFrom = searchCriteria.<T>getRangeFrom();
        var rangeTo = searchCriteria.<T>getRangeTo();

        switch (searchCriteria.getSearchType()) {
            case LIKE:
                return cb.or(searchLike(cb, value, searchCriteria.getValues()));
            case EQUAL:
                return cb.or(searchEqual(cb, value, searchCriteria.getValues()));
            case GREATER_THAN_OR_EQUAL_TO:
                return searchGreaterThanOrEqualTo(cb, value, rangeFrom);
            case LESS_THAN_OR_EQUAL_TO:
                return searchLessThanOrEqualTo(cb, value, rangeTo);
            case BETWEEN:
                return searchBetween(cb, value, rangeFrom, rangeTo);
            case IN:
                return searchIn(cb, value, searchCriteria.getValues());
            default:
                throw new NotImplementedException("Searching for search type '" + searchCriteria.getSearchType() + "' hasn't been implemented.");
        }
    }

    private <T extends Comparable<? super T>> Expression<T> getValueExpressionForDocPrefix(
        CriteriaBuilder cb,
        Root<JsonSchemaDocument> documentRoot,
        AdvancedSearchRequest.OtherFilter searchCriteria
    ) {
        var jsonPath = "$." + searchCriteria.getPath().substring(DOC_PREFIX.length());
        return queryDialectHelper.getJsonValueExpression(
            cb,
            documentRoot.get(CONTENT).get(CONTENT),
            jsonPath,
            searchCriteria.getDataType()
        );
    }

    private <T extends Comparable<? super T>> Expression<T> getValueExpressionForCasePrefix(
        Root<JsonSchemaDocument> documentRoot,
        AdvancedSearchRequest.OtherFilter searchCriteria
    ) {
        var documentColumnName = searchCriteria.getPath().substring(CASE_PREFIX.length());
        return documentRoot.get(documentColumnName).as(searchCriteria.getDataType());
    }

    @SuppressWarnings("unchecked")
    private <T> Predicate[] searchEqual(CriteriaBuilder cb, Expression<T> jsonValue, List<T> values) {
        if (values.isEmpty()) {
            return new Predicate[0];
        } else if (values.stream().anyMatch(value -> !(value instanceof String))) {
            return values.stream()
                .map(value -> cb.equal(jsonValue, value))
                .toArray(Predicate[]::new);
        } else {
            var jsonValueLower = cb.lower((Expression<String>) jsonValue);
            return values.stream()
                .map(value -> value.toString().trim().toLowerCase())
                .map(stringValue -> cb.equal(jsonValueLower, stringValue))
                .toArray(Predicate[]::new);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> Predicate[] searchLike(CriteriaBuilder cb, Expression<T> jsonValue, List<T> values) {
        if (values.isEmpty()) {
            return new Predicate[0];
        } else if (values.stream().anyMatch(value -> !(value instanceof String))) {
            throw new IllegalArgumentException("Failed to do LIKE search. Reason: values '" + Arrays.toString(values.toArray()) + "' aren't of type 'String'");
        } else {
            var jsonValueLower = cb.lower((Expression<String>) jsonValue);
            return values.stream()
                .map(value -> value.toString().trim().toLowerCase())
                .map(stringValue -> cb.like(jsonValueLower, "%" + stringValue + "%"))
                .toArray(Predicate[]::new);
        }
    }

    private <T> Predicate searchIn(CriteriaBuilder cb, Expression<T> jsonValue, List<T> values) {
        var in = cb.in(jsonValue);
        values.forEach(in::value);
        return in;
    }

    private <T extends Comparable<? super T>> Predicate searchGreaterThanOrEqualTo(CriteriaBuilder cb, Expression<T> documentValue, T rangeFrom) {
        if (rangeFrom instanceof TemporalAccessor) {
            var documentValueTimestamp = documentValue.as(java.util.Date.class);
            return cb.greaterThanOrEqualTo(documentValueTimestamp, toJavaUtilDate(rangeFrom));
        } else {
            return cb.greaterThanOrEqualTo(documentValue, cb.literal(rangeFrom));
        }
    }

    private <T extends Comparable<? super T>> Predicate searchLessThanOrEqualTo(CriteriaBuilder cb, Expression<T> documentValue, T rangeTo) {
        if (rangeTo instanceof TemporalAccessor) {
            var documentValueTimestamp = documentValue.as(java.util.Date.class);
            return cb.lessThanOrEqualTo(documentValueTimestamp, toJavaUtilDate(rangeTo));
        } else {
            return cb.lessThanOrEqualTo(documentValue, cb.literal(rangeTo));
        }
    }

    private <T extends Comparable<? super T>> Predicate searchBetween(CriteriaBuilder cb, Expression<T> documentValue, T rangeFrom, T rangeTo) {
        if (rangeFrom instanceof TemporalAccessor) {
            var documentValueTimestamp = documentValue.as(java.util.Date.class);
            return cb.between(documentValueTimestamp, toJavaUtilDate(rangeFrom), toJavaUtilDate(rangeTo));
        } else {
            return cb.between(documentValue, cb.literal(rangeFrom), cb.literal(rangeTo));
        }
    }

    /**
     * Note: The CriteriaBuilder only supports with java.sql.Timestamp/Date/Time. These types extend java.util.Date
     */
    private static java.util.Date toJavaUtilDate(Object value) {
        if (value instanceof LocalDate) {
            return java.util.Date.from(((LocalDate) value).atStartOfDay().toInstant(ZoneOffset.UTC));
        } else if (value instanceof Instant) {
            return java.sql.Timestamp.from((Instant) value);
        } else if (value instanceof LocalDateTime) {
            return java.sql.Timestamp.from(((LocalDateTime) value).toInstant(ZoneOffset.UTC));
        } else if (value instanceof OffsetDateTime) {
            return java.sql.Timestamp.from(((OffsetDateTime) value).toInstant());
        } else if (value instanceof ZonedDateTime) {
            return java.sql.Timestamp.from(((ZonedDateTime) value).toInstant());
        } else if (value instanceof LocalTime) {
            return java.sql.Time.valueOf((LocalTime) value);
        } else {
            throw new NotImplementedException("Failed to cast '" + value + "' to java.util.Date");
        }
    }

    private List<Order> getOrderBy(
        CriteriaBuilder cb,
        Root<JsonSchemaDocument> root,
        Sort sort
    ) {
        return sort.stream()
            .map(order -> {
                if (order.getProperty().startsWith(DOC_PREFIX)) {
                    var jsonPath = "$." + order.getProperty().substring(DOC_PREFIX.length());
                    return new OrderImpl(
                        queryDialectHelper.getJsonValueExpression(cb, root.get(CONTENT).get(CONTENT), jsonPath, String.class),
                        order.getDirection().isAscending()
                    );
                } else if (order.getProperty().startsWith(CASE_PREFIX)) {
                    return new OrderImpl(
                        root.get(order.getProperty().substring(CASE_PREFIX.length())),
                        order.getDirection().isAscending()
                    );
                } else if (order.getProperty().startsWith("$.")) {
                    return new OrderImpl(
                        cb.lower(queryDialectHelper.getJsonValueExpression(cb, root.get(CONTENT).get(CONTENT), order.getProperty(), String.class)), order.getDirection().isAscending()
                    );
                } else {
                    return new OrderImpl(
                        root.get(order.getProperty()),
                        order.getDirection().isAscending()
                    );
                }
            })
            .collect(Collectors.toList());
    }

    @FunctionalInterface
    public interface QueryWhereBuilder {
        void apply(
            CriteriaBuilder criteriaBuilder,
            CriteriaQuery<?> query,
            Root<JsonSchemaDocument> documentRoot
        );
    }
}

