package group

import common.Model

const val GROUP_INVALID_ID = -1

data class Group(
    val id: Int = GROUP_INVALID_ID,
    val name: String = ""
): Model<Group> {
    override fun trimmed(): Group {
        return copy(name = name.trim())
    }
}