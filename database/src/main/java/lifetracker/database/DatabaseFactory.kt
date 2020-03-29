package lifetracker.database

import com.squareup.sqldelight.ColumnAdapter
import com.squareup.sqldelight.db.SqlDriver
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter


object DatabaseFactory {

    fun buildDatabase(sqlDriver: SqlDriver) = Database(
        sqlDriver,
        Entry.Adapter(object : ColumnAdapter<LocalDate, String> {
            override fun decode(databaseValue: String): LocalDate =
                LocalDate.parse(databaseValue, DateTimeFormatter.ISO_LOCAL_DATE)

            override fun encode(value: LocalDate): String =
                value.format(DateTimeFormatter.ISO_LOCAL_DATE)
        })
    )

}
