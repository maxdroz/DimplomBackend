package discipline

const val DISCIPLINE_INVALID_ID = -1

data class Discipline(
    val id: Int = DISCIPLINE_INVALID_ID,
    val name: String = ""
)