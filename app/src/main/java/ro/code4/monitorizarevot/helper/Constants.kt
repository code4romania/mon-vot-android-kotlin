package ro.code4.monitorizarevot.helper


object Constants {
    const val DATE_TIME_FORMAT = "dd.MM.yyyy HH:mm"
    const val DATE_FORMAT = "yyyy-MM-dd HH:mm:ss"
    const val DATE_FORMAT_SIMPLE = "dd.MM.yyyy"
    const val DATE_ISO_8601_FORMAT = "yyyy-MM-dd'T'HH:mm'Z'"
    const val DATA_NOTE_FORMAT = "dd/MM HH:mm"
    const val FORM = "form"
    const val QUESTION = "question"
    const val NOTE = "note"
    const val FORM_QUESTION_CODES = "form_question_codes"

    const val REQUEST_CODE_RECORD_VIDEO = 1001
    const val REQUEST_CODE_TAKE_PHOTO = 1002
    const val REQUEST_CODE_GALLERY = 1003
    const val FILES_PATHS_SEPARATOR = "|"

    const val TYPE_MULTI_CHOICE = 0
    const val TYPE_SINGLE_CHOICE = 1
    const val TYPE_SINGLE_CHOICE_DETAILS = 2
    const val TYPE_MULTI_CHOICE_DETAILS = 3

    const val REMOTE_CONFIG_FILTER_DIASPORA_FORMS = "filter_diaspora_forms"
    const val REMOTE_CONFIG_CONTACT_EMAIL = "contact_email"
    const val REMOTE_CONFIG_PRIVACY_POLICY_URL = "privacy_policy_url"
    const val REMOTE_CONFIG_OBSERVER_GUIDE_URL = "observer_guide_url"
    const val REMOTE_CONFIG_SAFETY_GUIDE_URL = "safety_guide_url"
    const val REMOTE_CONFIG_OBSERVER_FEEDBACK_URL = "observer_feedback_url"
    const val REMOTE_CONFIG_ROUND_START_TIMESTAMP = "round_start_time"

    const val PUSH_NOTIFICATION_TITLE = "title"
    const val PUSH_NOTIFICATION_BODY = "body"
}
