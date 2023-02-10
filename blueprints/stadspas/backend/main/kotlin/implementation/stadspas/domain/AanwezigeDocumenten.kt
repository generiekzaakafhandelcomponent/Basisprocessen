package implementation.stadspas.domain

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class AanwezigeDocumenten(
    var jaarrekening: Boolean? = null,
    var balans: Boolean? = null,
    var aangifte: Boolean? = null,
    var aanslag: Boolean? = null
)
