package implementation.stadspas.domain

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class StadspasAanvraag(

    var aanvrager: Aanvrager? = Aanvrager(),
    var partner: Partner = Partner(),
    var kinderen: List<Kind> = arrayListOf(),
    var aanvraaggegevens: Aanvraaggegevens = Aanvraaggegevens(),
    var datumAanvraagOntvangen: String? = null,
    var inkomenKlant: Inkomen = Inkomen(),
    var inkomenPartner: Inkomen = Inkomen(),
    var vermogen: Vermogen = Vermogen(),
    var communicatievoorkeuren: List<String> = arrayListOf()
)
