package com.kamer.lifetracker

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import lifetracker.database.Data
import lifetracker.database.Entry
import lifetracker.database.EntryProperty
import lifetracker.database.Property
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import java.util.*


class UpdateDataUseCase(
    private val database: Data
) {

    suspend fun saveData(values: List<List<Any>>) = withContext(Dispatchers.IO) {
        val properties = newProperties(values.first().drop(1))
        val entries = values.drop(1)
            .map {
                LocalDate.parse(
                    it.first().toString(),
                    DateTimeFormatter.ofPattern("dd/MM/yyyy")
                )
            }
            .mapIndexed { index, entryDate ->
                Entry.Impl(
                    id = UUID.randomUUID().toString(),
                    date = entryDate,
                    position = index.toLong()
                )
            }
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

    private suspend fun newProperties(propertyNames: List<Any>): List<Property> {
        val existingProperties = database.getAllProperties()
        return propertyNames.mapIndexed { index, value ->
            val name = value.toString()
            val existingProperty = existingProperties.find { it.name == name }
            val id = existingProperty?.id ?: UUID.randomUUID().toString()
            Property.Impl(id, name, index.toLong())
        }
    }

}
