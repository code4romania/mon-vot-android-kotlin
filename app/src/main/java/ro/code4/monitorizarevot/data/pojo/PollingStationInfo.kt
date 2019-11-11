package ro.code4.monitorizarevot.data.pojo

data class PollingStationInfo(
    val countyName: String,
    val pollingStationNumber: Int,
    val isDiaspora: Boolean?
)