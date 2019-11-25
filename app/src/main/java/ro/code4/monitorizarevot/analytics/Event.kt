package ro.code4.monitorizarevot.analytics

enum class Event {
    SCREEN_OPEN,
    BUTTON_CLICK,
    MANUAL_SYNC
}

enum class ParamKey {
    NAME,
    NUMBER_NOT_SYNCED
}

data class Param(val key: ParamKey, val value: Any)