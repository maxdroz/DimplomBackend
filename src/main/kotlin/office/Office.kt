package office

const val OFFICE_INVALID_ID = -1

data class Office(
    val id: Int = OFFICE_INVALID_ID,
    val office: String = ""
)