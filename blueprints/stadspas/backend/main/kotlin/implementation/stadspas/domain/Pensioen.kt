package implementation.stadspas.domain

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Pensioen(

    var nettobedrag: Float? = null
)
