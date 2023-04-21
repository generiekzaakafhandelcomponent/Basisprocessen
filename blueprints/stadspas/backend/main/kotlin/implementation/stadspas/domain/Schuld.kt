package implementation.stadspas.domain

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Schuld(

    var bedrag: Float? = null,
    var schuldeiser: String? = null,
    var documenten: List<Any>? = listOf()
)
