package implementation.stadspas.domain

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class BedrijfsDocumenten(
    val aanwezigeDocumenten: AanwezigeDocumenten,
    var jaarrekening: List<Any>? = null,
    var balans: List<Any>? = null,
    var aangifte: List<Any>? = null,
    var aanslag: List<Any>? = null
)
