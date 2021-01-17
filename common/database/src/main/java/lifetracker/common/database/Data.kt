package lifetracker.common.database

import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import com.squareup.sqldelight.runtime.coroutines.mapToOne
import com.squareup.sqldelight.runtime.coroutines.mapToOneOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import lifetrackercommondatabase.Entry
import lifetrackercommondatabase.EntryPreview
import lifetrackercommondatabase.EntryPreviewByDate
import lifetrackercommondatabase.EntryProperties
import lifetrackercommondatabase.EntryProperty
import lifetrackercommondatabase.Property
import lifetrackercommondatabase.PropertyEntries
import org.threeten.bp.LocalDate


class Data(private val database: Database) {

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
                //room for errors because archived features may be filled
                val size = database.propertyQueries.size().executeAsOne()
                flowOf(entries.map { it to (it.count >= size) }.toMap())
            }

    fun getEntryStatus(date: LocalDate): Flow<Pair<EntryPreviewByDate, Long>> =
        database.entryQueries.entryPreviewByDate(date).asFlow().mapToOneOrNull()
            .filterNotNull()
            .map { entry ->
                val size = database.propertyQueries.size().executeAsOne()
                entry to size
            }

    fun getEntryProperties(entryId: String): Flow<List<EntryProperties>> =
        database.entryPropertyQueries.entryProperties(entryId).asFlow().mapToList()

    fun getPropertyEntries(propertyId: String): Flow<List<PropertyEntries>> =
        database.entryPropertyQueries.propertyEntries(propertyId).asFlow().mapToList()

    suspend fun getEntry(id: String): Entry = withContext(Dispatchers.IO) {
        database.entryQueries.findById(id).executeAsOne()
    }

    suspend fun getEntryByDate(date: LocalDate): Entry = withContext(Dispatchers.IO) {
        database.entryQueries.findByDate(date).executeAsOne()
    }

    fun observeEntryByDate(date: LocalDate): Flow<Entry> =
        database.entryQueries.findByDate(date).asFlow().mapToOne()

    suspend fun getEntryProperty(entryId: String, propertyId: String): EntryProperty? =
        withContext(Dispatchers.IO) {
            database.entryPropertyQueries.findById(entryId, propertyId).executeAsOneOrNull()
        }

    fun getProperties(): Flow<List<Property>> =
        database.propertyQueries.selectAll().asFlow().mapToList()

    suspend fun getAllProperties(): List<Property> = withContext(Dispatchers.IO) {
        database.propertyQueries.selectAll().executeAsList()
    }

    suspend fun getAllEntries(): List<Entry> = withContext(Dispatchers.IO) {
        database.entryQueries.selectAll().executeAsList()
    }

    suspend fun updateEntryPropertyValue(entryId: String, propertyId: String, value: Boolean?) =
        withContext(Dispatchers.IO) {
            database.entryPropertyQueries.updateValue(value, entryId, propertyId)
        }

    suspend fun createEntryProperty(entryId: String, propertyId: String, value: Boolean?) =
        withContext(Dispatchers.IO) {
            database.entryPropertyQueries.insert(EntryProperty(entryId, propertyId, value))
        }

    suspend fun getProperty(id: String): Property = withContext(Dispatchers.IO) {
        database.propertyQueries.findById(id).executeAsOne()
    }

}
