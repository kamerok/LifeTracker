package lifetracker.database

import android.content.Context
import androidx.sqlite.db.SupportSQLiteDatabase
import com.squareup.sqldelight.ColumnAdapter
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
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

    fun getEntries(): Flow<Map<EntryPreview, Boolean>> =
        database.entryQueries.entryPreview().asFlow().mapToList()
            .flatMapLatest { entries ->
                val size = database.propertyQueries.size().executeAsOne()
                flowOf(entries.map { it to (it.count == size) }.toMap())
            }

    fun getEntryProperties(entryId: String): Flow<List<EntryProperties>> =
        database.entryPropertyQueries.entryProperties(entryId).asFlow().mapToList()

    suspend fun getEntry(id: String): Entry = withContext(Dispatchers.IO) {
        database.entryQueries.findById(id).executeAsOne()
    }

    suspend fun getEntryByDate(date: LocalDate): Entry = withContext(Dispatchers.IO) {
        database.entryQueries.findByDate(date).executeAsOne()
    }

    suspend fun getEntryProperty(entryId: String, propertyId: String): EntryProperty? =
        withContext(Dispatchers.IO) {
            database.entryPropertyQueries.findById(entryId, propertyId).executeAsOneOrNull()
        }

    fun getProperties(): Flow<List<Property>> =
        database.propertyQueries.selectAll().asFlow().mapToList()

    suspend fun updateEntryPropertyValue(entryId: String, propertyId: String, value: Boolean?) =
        withContext(Dispatchers.IO) {
            database.entryPropertyQueries.updateValue(value, entryId, propertyId)
        }

    suspend fun createEntryProperty(entryId: String, propertyId: String, value: Boolean?) =
        withContext(Dispatchers.IO) {
            database.entryPropertyQueries.insert(EntryProperty.Impl(entryId, propertyId, value))
        }

    suspend fun getProperty(id: String): Property = withContext(Dispatchers.IO) {
        database.propertyQueries.findById(id).executeAsOne()
    }

}
