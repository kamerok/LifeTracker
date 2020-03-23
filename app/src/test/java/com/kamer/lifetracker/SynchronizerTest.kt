package com.kamer.lifetracker

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.squareup.sqldelight.ColumnAdapter
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver.Companion.IN_MEMORY
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
    private val database = buildInMemoryDatabase()
    private val data = Data(database)

    private val synchronizer = Synchronizer(
        UpdateDataUseCase(data),
        service
    )

    @Test
    fun `Properties should be empty on start`() = runBlocking {
        val properties = database.propertyQueries.selectAll().executeAsList()
        assertThat(properties).isEmpty()
    }

    @Test
    fun `Property should be created on first sync`() = runBlocking {
        whenever(service.getData()).thenReturn(listOf(listOf("", "Property")))

        synchronizer.sync()

        val properties = database.propertyQueries.selectAll().executeAsList()
        assertThat(properties).hasSize(1)
        val property = properties.first()
        assertThat(property.name).isEqualTo("Property")
        assertThat(property.position).isEqualTo(0)
    }

    @Test
    fun `Property id should be consistent between sync`() = runBlocking {
        whenever(service.getData()).thenReturn(listOf(listOf("", "Property")))

        synchronizer.sync()
        val firstSyncId = database.propertyQueries.selectAll().executeAsList().first().id
        synchronizer.sync()
        val secondSyncId = database.propertyQueries.selectAll().executeAsList().first().id

        assertThat(firstSyncId).isEqualTo(secondSyncId)
    }

    @Test
    fun `Property position should be updated after sync`() = runBlocking {
        val firstSync = listOf(listOf("", "Property1", "Property2"))
        val secondSync = listOf(listOf("", "Property2", "Property1"))
        whenever(service.getData()).thenReturn(firstSync, secondSync)

        synchronizer.sync()
        synchronizer.sync()
        val properties = database.propertyQueries.selectAll().executeAsList()
        val property1 = properties.find { it.name == "Property1" }!!
        val property2 = properties.find { it.name == "Property2" }!!

        assertThat(property2.position).isEqualTo(0)
        assertThat(property1.position).isEqualTo(1)
    }

    @Test
    fun `Missing properties should be deleted`() = runBlocking {
        val firstSync = listOf(listOf("", "Property1", "Property2"))
        val secondSync = listOf(listOf("", "Property2"))
        whenever(service.getData()).thenReturn(firstSync, secondSync)

        synchronizer.sync()
        synchronizer.sync()
        val properties = database.propertyQueries.selectAll().executeAsList()
        val property1 = properties.find { it.name == "Property1" }

        assertThat(property1).isNull()
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
