package office

import teacher.Teacher
import java.sql.ResultSet

class OfficeInteractor {
    companion object {
        fun ResultSet.getOffice(): Office {
            val id = getInt("id_office")
            val office = getString("office")
            return Office(id, office)
        }
    }
}