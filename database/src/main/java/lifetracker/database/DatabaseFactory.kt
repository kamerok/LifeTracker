package lifetracker.database

import android.content.Context
import androidx.sqlite.db.SupportSQLiteDatabase
import com.squareup.sqldelight.ColumnAdapter
import com.squareup.sqldelight.android.AndroidSqliteDriver
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter


object DatabaseFactory {

    fun buildDatabase(context: Context) = Database(
        AndroidSqliteDriver(
            schema = Database.Schema,
            context = context,
            name = "database.db",
            callback = object : AndroidSqliteDriver.Callback(Database.Schema) {
                override fun onOpen(db: SupportSQLiteDatabase) {
                    db.execSQL("PRAGMA foreign_keys=ON;")
                }
            }
        ),
        Entry.Adapter(object : ColumnAdapter<LocalDate, String> {
            override fun decode(databaseValue: String): LocalDate =
                LocalDate.parse(databaseValue, DateTimeFormatter.ISO_LOCAL_DATE)

            override fun encode(value: LocalDate): String =
                value.format(DateTimeFormatter.ISO_LOCAL_DATE)
        })
    )

}
