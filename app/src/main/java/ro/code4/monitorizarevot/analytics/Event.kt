package ro.code4.monitorizarevot.analytics

enum class Event(val title: String) {
    SCREEN_OPEN("screen_open"),
    BUTTON_CLICK("button_click"),
    MANUAL_SYNC("manual_sync"),
    LOGIN_FAILED("login_failed"),
    TAP_CALL("tap_call"),
    TAP_CHANGE_STATION("tap_change_station")
}

enum class ParamKey(val title: String) {
    NAME("name"),
    NUMBER_NOT_SYNCED("number_not_synced")
}

data class Param(val key: ParamKey, val value: Any)