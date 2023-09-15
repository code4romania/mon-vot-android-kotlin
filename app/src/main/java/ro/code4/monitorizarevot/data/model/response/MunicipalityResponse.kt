package ro.code4.monitorizarevot.data.model.response

import com.google.gson.annotations.Expose

class MunicipalityResponse {
    @Expose
    var id: Int = -1

    @Expose
    lateinit var  code: String

    @Expose
    lateinit var  name: String

    @Expose
    var numberOfPollingStations: Int = 0

    @Expose
    var order: Int = 0

    @Expose
    var countyId : Int = -1

    @Expose
    lateinit var countyCode : String

    @Expose
    lateinit var countyName: String

    @Expose
    var diaspora: Boolean =false

    @Expose
    var countyOrder: Int = 0
}