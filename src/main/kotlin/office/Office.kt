package office

import common.Model
import group.Group

const val OFFICE_INVALID_ID = -1

data class Office(
    val id: Int = OFFICE_INVALID_ID,
    val office: String = ""
): Model<Office> {
    override fun trimmed(): Office {
        return copy(office = office.trim())
    }
}