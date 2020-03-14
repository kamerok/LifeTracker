package com.kamer.lifetracker

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import lifetracker.database.*
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import java.util.*


class UpdateDataUseCase(
    private val database: Data
) {

    suspend fun saveData(values: List<List<Any>>) = withContext(Dispatchers.IO) {
        val properties = values.first().drop(1).mapIndexed { index, value ->
            Property.Impl(
                id = UUID.randomUUID().toString(),
                name = value.toString(),
                position = index.toLong()
            )
        }
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

}
