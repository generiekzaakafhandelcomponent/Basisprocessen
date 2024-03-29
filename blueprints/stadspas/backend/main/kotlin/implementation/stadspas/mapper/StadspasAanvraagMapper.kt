package implementation.stadspas.mapper

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.MissingNode
import com.fasterxml.jackson.module.kotlin.convertValue
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.ritense.document.service.DocumentService
import implementation.common.service.DocumentReaderService
import implementation.stadspas.domain.AandelenObligatie
import implementation.stadspas.domain.Aanvrager
import implementation.stadspas.domain.AanwezigeDocumenten
import implementation.stadspas.domain.Adres
import implementation.stadspas.domain.Bankrekening
import implementation.stadspas.domain.BedrijfsDocumenten
import implementation.stadspas.domain.CryptoCurrency
import implementation.stadspas.domain.Inkomen
import implementation.stadspas.domain.Kind
import implementation.stadspas.domain.StadspasAanvraag
import implementation.stadspas.domain.Partner
import implementation.stadspas.domain.Pensioen
import implementation.stadspas.domain.Schuld
import implementation.stadspas.domain.Uitkering
import implementation.stadspas.domain.Uitkeringen
import implementation.stadspas.domain.Vermogen
import implementation.stadspas.domain.WelkVermogen
import implementation.stadspas.domain.Werkgever
import implementation.stadspas.domain.Bezittingen

class StadspasAanvraagMapper(
    private val documentService: DocumentService,
    private val documentReaderService: DocumentReaderService
) {

    fun mapAanvragInDocumentByBusinessKey(businessKey: String) {

        documentReaderService.getDocumentById(businessKey)
            .apply {
                val stadspasAanvraag = mapAanvraag(
                    this.content().asJson()
                )
                val mappedAanvraag: JsonNode = jacksonObjectMapper().convertValue(
                    stadspasAanvraag
                )

                documentService.modifyDocument(
                    this,
                    mappedAanvraag
                )
            }
    }

    private fun mapAanvraag(jsonNode: JsonNode): StadspasAanvraag {

        return jsonNode.let {
            val persoonsgegevens: JsonNode? = when (val digidNode = it.at("/uw-gegevens-standaard-na/digid")) {
                is MissingNode -> null
                else -> digidNode
            } ?: when (val anoniemNode = it.at("/uw-gegevens-standaard-na/anoniem")) {
                is MissingNode -> null
                else -> anoniemNode
            }

            StadspasAanvraag(
                Aanvrager(
                    voornaam = persoonsgegevens?.getNodeValueByPathOrNull("/persoonsgegevensPrefill/voornaamPrefill")
                        ?: persoonsgegevens?.getNodeValueByPathOrNull("/persoonsgegevens/voornaam"),
                    tussenvoegsel = persoonsgegevens?.getNodeValueByPathOrNull("/persoonsgegevensPrefill/voorvoegselsPrefill")
                        ?: persoonsgegevens?.getNodeValueByPathOrNull("/persoonsgegevens/voorvoegsels"),
                    achternaam = persoonsgegevens?.getNodeValueByPathOrNull("/persoonsgegevensPrefill/achternaamPrefill")
                        ?: persoonsgegevens?.getNodeValueByPathOrNull("/persoonsgegevens/achternaam"),
                    adres = Adres(
                        straat = persoonsgegevens?.getNodeValueByPathOrNull("/adresgegevens1/straatnaamPrefill")
                            ?: persoonsgegevens?.getNodeValueByPathOrNull("/adresgegevens/straatnaam"),
                        huisnummer = persoonsgegevens?.getNodeValueByPathOrNull("/adresgegevens1/huisnummerPrefill")
                            ?: persoonsgegevens?.getNodeValueByPathOrNull("/adresgegevens/huisnummer"),
                        huisnummertoevoeging = persoonsgegevens?.getNodeValueByPathOrNull("/adresgegevens1/toevoegingHuisnummerPrefill")
                            ?: persoonsgegevens?.getNodeValueByPathOrNull("/adresgegevens/toevoegingHuisnummer"),
                        huisletter = persoonsgegevens?.getNodeValueByPathOrNull("/adresgegevens1/huisletterPrefill")
                            ?: persoonsgegevens?.getNodeValueByPathOrNull("/adresgegevens/huisletter"),
                        postcode = persoonsgegevens?.getNodeValueByPathOrNull("/adresgegevens1/postcodePrefill")
                            ?: persoonsgegevens?.getNodeValueByPathOrNull("/adresgegevens/postcode"),
                        woonplaats = persoonsgegevens?.getNodeValueByPathOrNull("/adresgegevens1/plaatsPrefill")
                            ?: persoonsgegevens?.getNodeValueByPathOrNull("/adresgegevens/plaats")
                    ),
                    telefoonnummer = it.getNodeValueByPathOrNull("/uw-gegevens-standaard-na/contactgegevens/telefoonnummer"),
                    emailadres = it.getNodeValueByPathOrNull("/uw-gegevens-standaard-na/contactgegevens/emailadres"),
                ),
                partner = Partner(
                    bsn = it.getNodeValueByPathOrNull("/stadspas-gezinssamenstelling/uwGezinssituatie/gegevensVanUwPartner/burgerservicenummerPartner"),
                    geboortedatum = it.getNodeValueByPathOrNull("/stadspas-gezinssamenstelling/uwGezinssituatie/gegevensVanUwPartner/geboortedatumPartner"),
                    voorletters = it.getNodeValueByPathOrNull("/stadspas-gezinssamenstelling/uwGezinssituatie/gegevensVanUwPartner/voorletterPartner"),
                    tussenvoegsel = it.getNodeValueByPathOrNull("/stadspas-gezinssamenstelling/uwGezinssituatie/gegevensVanUwPartner/voorvoegselPartner"),
                    achternaam = it.getNodeValueByPathOrNull("/stadspas-gezinssamenstelling/uwGezinssituatie/gegevensVanUwPartner/achternaamPartner"),
                    identiteitsbewijs = it.getNodeValueByPathOrNull("/stadspas-gezinssamenstelling/uwGezinssituatie/gegevensVanUwPartner/idPartner")
                ),
                kinderen = it.getNodeValueByPathOrNull<List<JsonNode>?>("/stadspas-gezinssamenstelling/uwGezinssituatie/gegevensVanUwKinderen/kinderen")
                    ?.map {
                        Kind(
                            voorletters = it.getNodeValueByPathOrNull("/voorletterKind"),
                            voornaam = it.getNodeValueByPathOrNull("/voornamenKind"),
                            tussenvoegsel = it.getNodeValueByPathOrNull("/voorvoegselKind"),
                            achternaam = it.getNodeValueByPathOrNull("/achternaamKind"),
                            bsn = it.getNodeValueByPathOrNull("/burgerservicenummerKind"),
                            geboortedatum = it.getNodeValueByPathOrNull("/geboortedatumKind")
                        )
                    }
                    ?: listOf(),
                aanvraaggegevens = Aanvraaggegevens(
                    heeftBijstandsuitkering = it.getNodeValueByPathOrNull<String?>("/stadspas-uw-situatie/OntvangtBijstandsuitkering")
                        .asJaNeeOrNvt(),
                    heeftSchuldhulpverlening = it.getNodeValueByPathOrNull<String?>("/stadspas-uw-situatie/inSchuldhulpverlening")
                        .asJaNeeOrNvt(),
                    heeftInwonendePartner = it.getNodeValueByPathOrNull<String?>("/stadspas-gezinssamenstelling/uwGezinssituatie/inwonendePartner")
                        .asJaNeeOrNvt(),
                    heeftInwonendeKinderen = it.getNodeValueByPathOrNull<String?>("/stadspas-gezinssamenstelling/uwGezinssituatie/inwonendeKinderen18")
                        .asJaNeeOrNvt(),
                    aanvragerIsStudent = it.getNodeValueByPathOrNull<String?>("/stadspas-gezinssamenstelling/uwStudiesituatie/HOStudent")
                        .asJaNeeOrNvt(),
                    partnerIsStudent = it.getNodeValueByPathOrNull<String?>("/stadspas-gezinssamenstelling/uwStudiesituatie/HOStudentPartner")
                        .asJaNeeOrNvt(),
                    aanvragerIsOndernemer = it.getNodeValueByPathOrNull<String?>("/stadspas-uw-inkomsten/ondernemer/bentUOndernemer")
                        .asJaNeeOrNvt(),
                    heeftHuurinkomsten = it.getNodeValueByPathOrNull<String?>("/stadspas-uw-inkomsten/inkomenUitOnderhuur/heeftUInkomenUitOnderhuur")
                        .asJaNeeOrNvt(),
                    heeftWerkgever = it.getNodeValueByPathOrNull<String?>("/stadspas-uw-inkomsten/heeftUEenWerkgever")
                        .asJaNeeOrNvt(),
                    heeftPensioen = it.getNodeValueByPathOrNull<String?>("/stadspas-uw-inkomsten/pensioenenVeldenGroep/krijgtUPensioen")
                        .asJaNeeOrNvt(),
                    heeftKinderalimentatie = it.getNodeValueByPathOrNull<String?>("/stadspas-uw-inkomsten/alimentatie/krijgtUKinderalimentatie")
                        .asJaNeeOrNvt(),
                    partnerIsOndernemer = it.getNodeValueByPathOrNull<String?>("/stadspas-inkomsten-partner/OndernemerPartner/isUwPartnerOndernemer")
                        .asJaNeeOrNvt(),
                    heeftPartnerHuurinkomsten = it.getNodeValueByPathOrNull<String?>("/stadspas-inkomsten-partner/inkomenUitOnderhuurPartner/heeftUwPartnerInkomenUitOnderhuur")
                        .asJaNeeOrNvt(),
                    heeftPartnerWerkgever = it.getNodeValueByPathOrNull<String?>("/stadspas-inkomsten-partner/inkomstenVanUwPartner/heeftUwPartnerEenWerkgever")
                        .asJaNeeOrNvt(),
                    heeftPartnerPensioen = it.getNodeValueByPathOrNull<String?>("/stadspas-inkomsten-partner/pensioenenPartnerVeldenGroep/krijgtUwPartnerPensioen")
                        .asJaNeeOrNvt(),
                    heeftPartnerKinderalimentatie = it.getNodeValueByPathOrNull<String?>("/stadspas-inkomsten-partner/alimentatiePartnerGroep/krijgtUwPartnerKinderAlimentatie")
                        .asJaNeeOrNvt()
                ),
                inkomenKlant = Inkomen(
                    werkgevers = it.getNodeValueByPathOrNull<List<JsonNode>?>("/stadspas-uw-inkomsten/loondienstWerkgevers/werkgevers")
                        ?.map {
                            Werkgever(
                                naam = it.getNodeValueByPathOrNull("/naamWerkgever"),
                                periode = it.getNodeValueByPathOrNull("/periodeNettoLoon"),
                                nettoloon = it.getNodeValueByPathOrNull("/nettoLoon")
                            )
                        }
                        ?: listOf(),
                    uitkeringen = Uitkeringen(
                        aow = Uitkering(
                            actief = it.getNodeValueByPathOrNull("/stadspas-uw-inkomsten/uitkeringenVeldenGroep/uitkeringen/aow"),
                            naam = "AOW",
                            periode = it.getNodeValueByPathOrNull("/stadspas-uw-inkomsten/inkomenGroepAOW/periodeBedragAOW"),
                            nettobedrag = it.getNodeValueByPathOrNull("/stadspas-uw-inkomsten/inkomenGroepAOW/nettoBedragAOW")
                        ),
                        anw = Uitkering(
                            actief = it.getNodeValueByPathOrNull("/stadspas-uw-inkomsten/uitkeringenVeldenGroep/uitkeringen/anw"),
                            naam = "ANW",
                            periode = it.getNodeValueByPathOrNull("/stadspas-uw-inkomsten/inkomenGroepANW/periodeBedragANW"),
                            nettobedrag = it.getNodeValueByPathOrNull("/stadspas-uw-inkomsten/inkomenGroepANW/nettoBedragANW")
                        ),
                        bijstand = Uitkering(
                            actief = it.getNodeValueByPathOrNull("/stadspas-uw-inkomsten/uitkeringenVeldenGroep/uitkeringen/bijstand"),
                            naam = "Bisjstand",
                            periode = it.getNodeValueByPathOrNull("/stadspas-uw-inkomsten/inkomenGroepBijstand/periodeBedragBijstand"),
                            nettobedrag = it.getNodeValueByPathOrNull("/stadspas-uw-inkomsten/inkomenGroepBijstand/nettoBedragBijstand")
                        ),
                        wao = Uitkering(
                            actief = it.getNodeValueByPathOrNull("/stadspas-uw-inkomsten/uitkeringenVeldenGroep/uitkeringen/wao"),
                            naam = "WAO",
                            periode = it.getNodeValueByPathOrNull("/stadspas-uw-inkomsten/inkomenGroepWAO/periodeBedragWAO"),
                            nettobedrag = it.getNodeValueByPathOrNull("/stadspas-uw-inkomsten/inkomenGroepWAO/nettoBedragWAO")
                        ),
                        ww = Uitkering(
                            actief = it.getNodeValueByPathOrNull("/stadspas-uw-inkomsten/uitkeringenVeldenGroep/uitkeringen/ww"),
                            naam = "WW",
                            periode = it.getNodeValueByPathOrNull("/stadspas-uw-inkomsten/inkomenGroepWW/periodeBedragWW"),
                            nettobedrag = it.getNodeValueByPathOrNull("/stadspas-uw-inkomsten/inkomenGroepWW/nettoBedragWW")
                        ),
                        wajong = Uitkering(
                            actief = it.getNodeValueByPathOrNull("/stadspas-uw-inkomsten/uitkeringenVeldenGroep/uitkeringen/wajong"),
                            naam = "Wajong",
                            periode = it.getNodeValueByPathOrNull("/stadspas-uw-inkomsten/inkomenGroepWajong/periodeBedragWajong"),
                            nettobedrag = it.getNodeValueByPathOrNull("/stadspas-uw-inkomsten/inkomenGroepWajong/nettoBedragWajong")
                        ),
                        wia = Uitkering(
                            actief = it.getNodeValueByPathOrNull("/stadspas-uw-inkomsten/uitkeringenVeldenGroep/uitkeringen/wia"),
                            naam = "WIA",
                            periode = it.getNodeValueByPathOrNull("/stadspas-uw-inkomsten/inkomenGroepWIA/periodeBedragWIA"),
                            nettobedrag = it.getNodeValueByPathOrNull("/stadspas-uw-inkomsten/inkomenGroepWIA/nettoBedragWIA")
                        ),
                        ziektewet = Uitkering(
                            actief = it.getNodeValueByPathOrNull("/stadspas-uw-inkomsten/uitkeringenVeldenGroep/uitkeringen/ziektewet"),
                            naam = "Ziektewet",
                            periode = it.getNodeValueByPathOrNull("/stadspas-uw-inkomsten/inkomenGroepZiektewet/periodeBedragZiektewet"),
                            nettobedrag = it.getNodeValueByPathOrNull("/stadspas-uw-inkomsten/inkomenGroepZiektewet/nettoBedragZiektewet")
                        )
                    ),
                    pensioenen = it.getNodeValueByPathOrNull<List<JsonNode>?>("/stadspas-uw-inkomsten/pensioenenVeldenGroep/pensioenenGroep/pensioenen")
                        ?.map {
                            Pensioen(
                                nettobedrag = it.getNodeValueByPathOrNull("/nettoPensioen")
                            )
                        }
                        ?: listOf(),
                    nettoKinderalimentatie = it.getNodeValueByPathOrNull("/stadspas-uw-inkomsten/alimentatie/nettoKinderalimentatie"),
                    bedragOnderhuur = it.getNodeValueByPathOrNull("/stadspas-uw-inkomsten/inkomenUitOnderhuur/bedragOnderhuur"),
                    bedrijfsDocumenten = BedrijfsDocumenten(
                        aanwezigeDocumenten = AanwezigeDocumenten(
                            jaarrekening = it.getNodeValueByPathOrNull("/stadspas-uw-inkomsten/ondernemer/uploadBedrijfsDocumenten/a"),
                            balans = it.getNodeValueByPathOrNull("/stadspas-uw-inkomsten/ondernemer/uploadBedrijfsDocumenten/b"),
                            aangifte = it.getNodeValueByPathOrNull("/stadspas-uw-inkomsten/ondernemer/uploadBedrijfsDocumenten/c"),
                            aanslag = it.getNodeValueByPathOrNull("/stadspas-uw-inkomsten/ondernemer/uploadBedrijfsDocumenten/d")
                        ),
                        jaarrekening = it.getNodeValueByPathOrNull("/stadspas-uw-inkomsten/ondernemer/jaarrekening"),
                        balans = it.getNodeValueByPathOrNull("/stadspas-uw-inkomsten/ondernemer/balans"),
                        aangifte = it.getNodeValueByPathOrNull("/stadspas-uw-inkomsten/ondernemer/aangifte"),
                        aanslag = it.getNodeValueByPathOrNull("/stadspas-uw-inkomsten/ondernemer/aanslag")
                    )
                ),
                inkomenPartner = Inkomen(
                    werkgevers = it.getNodeValueByPathOrNull<List<JsonNode>?>("/stadspas-inkomsten-partner/loondienstWerkgeverSPartner/werkgeversPartner")
                        ?.map {
                            Werkgever(
                                naam = it.getNodeValueByPathOrNull("/naamWerkgeverPartner"),
                                periode = it.getNodeValueByPathOrNull("/periodeNettoLoonPartner"),
                                nettoloon = it.getNodeValueByPathOrNull("/nettoLoonPartner")
                            )
                        }
                        ?: listOf(),
                    uitkeringen = Uitkeringen(
                        aow = Uitkering(
                            actief = it.getNodeValueByPathOrNull("/stadspas-inkomsten-partner/uitkeringenPartnerVeldenGroep/uitkeringenPartner/aow"),
                            naam = "AOW",
                            periode = it.getNodeValueByPathOrNull("/stadspas-inkomsten-partner/uitkeringenPartnerVeldenGroep/aowPartner/periodeBedragAOWPartner"),
                            nettobedrag = it.getNodeValueByPathOrNull("/stadspas-inkomsten-partner/uitkeringenPartnerVeldenGroep/aowPartner/nettoBedragAOWPartner")
                        ),
                        anw = Uitkering(
                            actief = it.getNodeValueByPathOrNull("/stadspas-inkomsten-partner/uitkeringenPartnerVeldenGroep/uitkeringenPartner/anw"),
                            naam = "ANW",
                            periode = it.getNodeValueByPathOrNull("/stadspas-inkomsten-partner/uitkeringenPartnerVeldenGroep/ANWPartner/periodeBedragANWPartner"),
                            nettobedrag = it.getNodeValueByPathOrNull("/stadspas-inkomsten-partner/uitkeringenPartnerVeldenGroep/ANWPartner/nettoBedragANWPartner")
                        ),
                        bijstand = Uitkering(
                            actief = it.getNodeValueByPathOrNull("/stadspas-inkomsten-partner/uitkeringenPartnerVeldenGroep/uitkeringenPartner/bijstand"),
                            naam = "Bisjstand",
                            periode = it.getNodeValueByPathOrNull("/stadspas-inkomsten-partner/uitkeringenPartnerVeldenGroep/BijstandPartner/periodeBedragBijstandPartner"),
                            nettobedrag = it.getNodeValueByPathOrNull("/stadspas-inkomsten-partner/uitkeringenPartnerVeldenGroep/BijstandPartner/nettoBedragBijstandPartner")
                        ),
                        wao = Uitkering(
                            actief = it.getNodeValueByPathOrNull("/stadspas-inkomsten-partner/uitkeringenPartnerVeldenGroep/uitkeringenPartner/wao"),
                            naam = "WAO",
                            periode = it.getNodeValueByPathOrNull("/stadspas-inkomsten-partner/uitkeringenPartnerVeldenGroep/WAOPartner/periodeBedragWAOPartner"),
                            nettobedrag = it.getNodeValueByPathOrNull("/stadspas-inkomsten-partner/uitkeringenPartnerVeldenGroep/WAOPartner/nettoBedragWAOPartner")
                        ),
                        ww = Uitkering(
                            actief = it.getNodeValueByPathOrNull("/stadspas-inkomsten-partner/uitkeringenPartnerVeldenGroep/uitkeringenPartner/ww"),
                            naam = "WW",
                            periode = it.getNodeValueByPathOrNull("/stadspas-inkomsten-partner/uitkeringenPartnerVeldenGroep/WWPartner/periodeBedragWWPartner"),
                            nettobedrag = it.getNodeValueByPathOrNull("/stadspas-inkomsten-partner/uitkeringenPartnerVeldenGroep/WWPartner/nettoBedragWWPartner")
                        ),
                        wajong = Uitkering(
                            actief = it.getNodeValueByPathOrNull("/stadspas-inkomsten-partner/uitkeringenPartnerVeldenGroep/uitkeringenPartner/wajong"),
                            naam = "Wajong",
                            periode = it.getNodeValueByPathOrNull("/stadspas-inkomsten-partner/uitkeringenPartnerVeldenGroep/WajongPartner/periodeBedragWajongPartner"),
                            nettobedrag = it.getNodeValueByPathOrNull("/stadspas-inkomsten-partner/uitkeringenPartnerVeldenGroep/WajongPartner/nettoBedragWajongPartner")
                        ),
                        wia = Uitkering(
                            actief = it.getNodeValueByPathOrNull("/stadspas-inkomsten-partner/uitkeringenPartnerVeldenGroep/uitkeringenPartner/wia"),
                            naam = "WIA",
                            periode = it.getNodeValueByPathOrNull("/stadspas-inkomsten-partner/uitkeringenPartnerVeldenGroep/WIAPartner/periodeBedragWIAPartner"),
                            nettobedrag = it.getNodeValueByPathOrNull("/stadspas-inkomsten-partner/uitkeringenPartnerVeldenGroep/WIAPartner/nettoBedragWIAPartner")
                        ),
                        ziektewet = Uitkering(
                            actief = it.getNodeValueByPathOrNull("/stadspas-inkomsten-partner/uitkeringenPartnerVeldenGroep/uitkeringenPartner/ziektewet"),
                            naam = "Ziektewet",
                            periode = it.getNodeValueByPathOrNull("/stadspas-inkomsten-partner/uitkeringenPartnerVeldenGroep/ZiektewetPartner/periodeBedragZiektewetPartner"),
                            nettobedrag = it.getNodeValueByPathOrNull("/stadspas-inkomsten-partner/uitkeringenPartnerVeldenGroep/ZiektewetPartner/nettoBedragZiektewetPartner")
                        )
                    ),
                    pensioenen = it.getNodeValueByPathOrNull<List<JsonNode>?>("/stadspas-inkomsten-partner/pensioenenPartnerVeldenGroep/PensioenPartner/pensioenenPartner")
                        ?.map {
                            Pensioen(
                                nettobedrag = it.getNodeValueByPathOrNull("/nettoPensioenPartner")
                            )
                        }
                        ?: listOf(),
                    nettoKinderalimentatie = it.getNodeValueByPathOrNull("/stadspas-inkomsten-partner/alimentatiePartnerGroep/nettoKinderalimentatiePartner"),
                    bedragOnderhuur = it.getNodeValueByPathOrNull("/stadspas-inkomsten-partner/inkomenUitOnderhuurPartner/bedragOnderhuurPartner"),
                    bedrijfsDocumenten = BedrijfsDocumenten(
                        aanwezigeDocumenten = AanwezigeDocumenten(
                            jaarrekening = it.getNodeValueByPathOrNull("/stadspas-inkomsten-partner/OndernemerPartner/uploadBedrijfsDocumentenPartner/a"),
                            balans = it.getNodeValueByPathOrNull("/stadspas-inkomsten-partner/OndernemerPartner/uploadBedrijfsDocumentenPartner/b"),
                            aangifte = it.getNodeValueByPathOrNull("/stadspas-inkomsten-partner/OndernemerPartner/uploadBedrijfsDocumentenPartner/c"),
                            aanslag = it.getNodeValueByPathOrNull("/stadspas-inkomsten-partner/OndernemerPartner/uploadBedrijfsDocumentenPartner/d")
                        ),
                        jaarrekening = it.getNodeValueByPathOrNull("/stadspas-inkomsten-partner/OndernemerPartner/JaarrekeningPartner"),
                        balans = it.getNodeValueByPathOrNull("/stadspas-inkomsten-partner/OndernemerPartner/BalansPartner"),
                        aangifte = it.getNodeValueByPathOrNull("/stadspas-inkomsten-partner/OndernemerPartner/AangiftePartner"),
                        aanslag = it.getNodeValueByPathOrNull("/stadspas-inkomsten-partner/OndernemerPartner/AanslagPartner")
                    )
                ),
                vermogen = Vermogen(
                    welkVermogen = WelkVermogen(
                        bankrekeningen = it.getNodeValueByPathOrNull("/stadspas-bezit-en-schulden/uwVermogenGroep/welkVermogen/a"),
                        aandelenObligaties = it.getNodeValueByPathOrNull("/stadspas-bezit-en-schulden/uwVermogenGroep/welkVermogen/b"),
                        crypto = it.getNodeValueByPathOrNull("/stadspas-bezit-en-schulden/uwVermogenGroep/welkVermogen/c"),
                        contanten = it.getNodeValueByPathOrNull("/stadspas-bezit-en-schulden/uwVermogenGroep/welkVermogen/d")
                    ),
                    bezittingen = Bezittingen(
                        bankrekeningen = it.getNodeValueByPathOrNull<List<JsonNode>?>("/stadspas-bezit-en-schulden/rekeningGroep/rekeningen")
                            ?.map {
                                Bankrekening(
                                    iban = it.getNodeValueByPathOrNull("/iban"),
                                    tegoed = it.getNodeValueByPathOrNull("/saldo"),
                                    documenten = it.getNodeValueByPathOrNull("/uploadAfschrift")
                                )
                            }
                            ?: listOf(),
                        aandelenObligaties = it.getNodeValueByPathOrNull<List<JsonNode>?>("/stadspas-bezit-en-schulden/obligatieGroep/obligaties")
                            ?.map {
                                AandelenObligatie(
                                    waarde = it.getNodeValueByPathOrNull("/waardeObligatie"),
                                    documenten = it.getNodeValueByPathOrNull("/fileObligatie")
                                )
                            }
                            ?: listOf(),
                        cryptoCurrencies = it.getNodeValueByPathOrNull<List<JsonNode>?>("/stadspas-bezit-en-schulden/cryptoGroep/crypto")
                            ?.map {
                                CryptoCurrency(
                                    waarde = it.getNodeValueByPathOrNull("/waardeCrypto"),
                                    documenten = it.getNodeValueByPathOrNull("/filecrypto")
                                )
                            }
                            ?: listOf(),
                        contantGeld = it.getNodeValueByPathOrNull("/stadspas-bezit-en-schulden/contanten/contantGeld")
                    ),
                    schulden = it.getNodeValueByPathOrNull<List<JsonNode>?>("/stadspas-bezit-en-schulden/schuldenGroep/vragenSchulden/schulden")
                        ?.map {
                            Schuld(
                                bedrag = it.getNodeValueByPathOrNull("/schuld"),
                                schuldeiser = it.getNodeValueByPathOrNull("/bijWieHeeftUSchuldOfBetalingsachterstand"),
                                documenten = it.getNodeValueByPathOrNull("/fileBetalingsachterstand")
                            )
                        }
                        ?: listOf()
                )
            )
        }
    }

    private inline fun <reified T> JsonNode.getNodeValueByPathOrNull(path: String): T? {
        return when (val nodeAtPath = this.at(path)) {
            is MissingNode -> null
            else -> jacksonObjectMapper().convertValue(nodeAtPath)
        }
    }

    /**
     * Attempt to convert this [String] into a [Boolean].
     * @return [Boolean] value corresponding to this [String] or null.
     */
    private fun String.asBooleanOrNull(): Boolean? {
        return when (this) {
            "ja", "j" -> true
            "nee", "n" -> false
            else -> null
        }
    }

    private fun String?.asJaNeeOrNvt(): String {
        return when (this) {
            "ja", "j" -> "ja"
            "nee", "n" -> "nee"
            else -> "nvt"
        }
    }

}
