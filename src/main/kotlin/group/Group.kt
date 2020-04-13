package group

const val GROUP_INVALID_ID = -1

data class Group(
    val id: Int = GROUP_INVALID_ID,
    val name: String = ""
)