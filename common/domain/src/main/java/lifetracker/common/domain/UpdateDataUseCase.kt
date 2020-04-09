package lifetracker.common.domain

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import lifetracker.common.database.Data
import lifetracker.common.database.Entry
import lifetracker.common.database.EntryProperty
import lifetracker.common.database.Property
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
                EntryProperty.Impl(
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
            val existingProperty = existingProperties.find { it.name == name }
            Property.Impl(
                id = existingProperty?.id ?: UUID.randomUUID().toString(),
                name = name,
                position = index.toLong()
            )
        }
    }

    private suspend fun buildEntries(entryDates: List<LocalDate>): List<Entry> {
        val existingEntries = database.getAllEntries()
        return entryDates.mapIndexed { index, entryDate ->
            val existingEntry = existingEntries.find { it.date == entryDate }
            Entry.Impl(
                id = existingEntry?.id ?: UUID.randomUUID().toString(),
                date = entryDate,
                position = index.toLong()
            )
        }
    }

}
