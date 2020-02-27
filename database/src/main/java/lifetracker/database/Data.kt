package lifetracker.database

import android.content.Context
import androidx.sqlite.db.SupportSQLiteDatabase
import com.squareup.sqldelight.ColumnAdapter
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter


class Data(context: Context) {

    private val database = Database(
        AndroidSqliteDriver(
            schema = Database.Schema,
            context = context,
            name = "database.db",
            callback = object : AndroidSqliteDriver.Callback(Database.Schema) {
                override fun onOpen(db: SupportSQLiteDatabase) {
                    db.execSQL("PRAGMA foreign_keys=ON;");
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

    suspend fun setData(
        properties: List<Property>,
        entries: List<Entry>,
        entryProperties: List<EntryProperty>
    ) = withContext(Dispatchers.IO) {
        database.transaction {
            database.entryPropertyQueries.clear()
            database.propertyQueries.clear()
            database.entryQueries.clear()
            entries.forEach { database.entryQueries.insert(it) }
            properties.forEach { database.propertyQueries.insert(it) }
            entryProperties.forEach { database.entryPropertyQueries.insert(it) }
        }
    }

    fun getEntries(): Flow<List<EntryPreview>> =
        database.entryQueries.entryPreview().asFlow().mapToList()

}
