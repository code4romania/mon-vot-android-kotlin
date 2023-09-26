package ro.code4.monitorizarevot.data.pojo

data class PollingStationInfo(
    val provinceName: String,
    val countyName: String,
    val municipalityName: String,
    val pollingStationNumber: Int,
    val isDiaspora: Boolean?
)