package com.kamer.lifetracker

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.squareup.sqldelight.ColumnAdapter
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver.Companion.IN_MEMORY
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import lifetracker.database.Data
import lifetracker.database.Database
import lifetracker.database.Entry
import org.junit.Test
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter

class SynchronizerTest {

    private val service: Service = mock {
        onBlocking { getData() }.doReturn(listOf(listOf()))
    }
    private val database = Data(buildInMemoryDatabase())

    private val synchronizer = Synchronizer(
        UpdateDataUseCase(database),
        service
    )

    @Test
    fun `Properties should be empty on start`() = runBlocking {
        val properties = database.getProperties().take(1).toList().first()
        assertThat(properties).isEmpty()
    }

    @Test
    fun `Property should be created on sync`() = runBlocking {
        whenever(service.getData()).thenReturn(listOf(listOf("", "Property")))

        synchronizer.sync()

        val properties = database.getProperties().take(1).toList().first()
        assertThat(properties).hasSize(1)
        val property = properties.first()
        assertThat(property.name).isEqualTo("Property")
        assertThat(property.position).isEqualTo(0)
    }

    @Test
    fun `Id should be consistent between sync`() = runBlocking {
        whenever(service.getData()).thenReturn(listOf(listOf("", "Property")))

        synchronizer.sync()
        val firstSyncId = database.getProperties().take(1).toList().first().first().id
        synchronizer.sync()
        val secondSyncId = database.getProperties().take(1).toList().first().first().id

        assertThat(firstSyncId).isEqualTo(secondSyncId)
    }

    private fun buildInMemoryDatabase(): Database {
        val driver = JdbcSqliteDriver(IN_MEMORY)
        Database.Schema.create(driver)
        return Database(
            driver,
            Entry.Adapter(object : ColumnAdapter<LocalDate, String> {
                override fun decode(databaseValue: String): LocalDate =
                    LocalDate.parse(databaseValue, DateTimeFormatter.ISO_LOCAL_DATE)

                override fun encode(value: LocalDate): String =
                    value.format(DateTimeFormatter.ISO_LOCAL_DATE)
            })
        )
    }
}
