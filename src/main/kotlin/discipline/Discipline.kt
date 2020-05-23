package discipline

import common.Model

const val DISCIPLINE_INVALID_ID = -1

data class Discipline(
    val id: Int = DISCIPLINE_INVALID_ID,
    val name: String = "",
    val shortName: String = ""
): Model<Discipline> {
    override fun trimmed(): Discipline {
        return this.copy(name = name.trim(), shortName = shortName.trim())
    }
}