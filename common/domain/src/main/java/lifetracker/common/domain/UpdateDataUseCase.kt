package lifetracker.common.domain

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import lifetracker.common.database.Data
import lifetrackercommondatabase.Entry
import lifetrackercommondatabase.EntryProperty
import lifetrackercommondatabase.Property
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import java.util.*


class UpdateDataUseCase(
    private val database: Data
) {

    suspend fun saveData(values: List<List<Any>>) = withContext(Dispatchers.IO) {
        val properties = buildProperties(values.first().drop(1))
        val entryDates = values.drop(1)
            .map {
                LocalDate.parse(
                    it.first().toString(),
                    DateTimeFormatter.ofPattern("dd/MM/yyyy")
                )
            }
        val entries = buildEntries(entryDates)
        val entryProperties = values.drop(1).withIndex().flatMap { row ->
            val entryValues = row.value.drop(1)
            entryValues.mapIndexed { index, value ->
                EntryProperty(
                    entryId = entries[row.index].id,
                    propertyId = properties[index].id,
                    value = when (value) {
                        "Y" -> true
                        "N" -> false
                        else -> null
                    }
                )
            }
        }
        database.setData(
            properties,
            entries,
            entryProperties
        )
    }

    private suspend fun buildProperties(propertyNames: List<Any>): List<Property> {
        val existingProperties = database.getAllProperties()
        return propertyNames.mapIndexed { index, value ->
            val name = value.toString()
            val isArchived = name.startsWith(ARCHIVED_MARKER)
            val existingProperty = existingProperties.find { it.name == name }
            Property(
                id = existingProperty?.id ?: UUID.randomUUID().toString(),
                name = name.substringAfter(ARCHIVED_MARKER),
                position = index.toLong(),
                isArchived = isArchived
            )
        }
    }

    private suspend fun buildEntries(entryDates: List<LocalDate>): List<Entry> {
        val existingEntries = database.getAllEntries()
        return entryDates.mapIndexed { index, entryDate ->
            val existingEntry = existingEntries.find { it.date == entryDate }
            Entry(
                id = existingEntry?.id ?: UUID.randomUUID().toString(),
                date = entryDate,
                position = index.toLong()
            )
        }
    }

    companion object {
        private const val ARCHIVED_MARKER = "[Archived]"
    }

}
