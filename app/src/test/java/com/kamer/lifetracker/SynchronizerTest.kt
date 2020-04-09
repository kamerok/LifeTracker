package com.kamer.lifetracker

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver.Companion.IN_MEMORY
import kotlinx.coroutines.runBlocking
import lifetracker.common.database.Data
import lifetracker.common.database.Database
import lifetracker.common.database.DatabaseFactory
import org.junit.Test
import org.threeten.bp.LocalDate
import org.threeten.bp.Month

class SynchronizerTest {

    private val service: SpreadsheetService = mock {
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

    @Test
    fun `Entries should be empty on start`() = runBlocking {
        val entries = database.entryQueries.selectAll().executeAsList()
        assertThat(entries).isEmpty()
    }

    @Test
    fun `Entry should be created on first sync`() = runBlocking {
        whenever(service.getData()).thenReturn(
            listOf(
                listOf("", "Property"),
                listOf("10/02/2019")
            )
        )

        synchronizer.sync()

        val entries = database.entryQueries.selectAll().executeAsList()
        assertThat(entries).hasSize(1)
        val entry = entries.first()
        assertThat(entry.date).isEqualTo(LocalDate.of(2019, Month.FEBRUARY, 10))
        assertThat(entry.position).isEqualTo(0)
    }

    @Test
    fun `Entry id should be consistent between sync`() = runBlocking {
        whenever(service.getData()).thenReturn(
            listOf(
                listOf("", "Property"),
                listOf("10/02/2019")
            )
        )

        synchronizer.sync()
        val firstSyncId = database.entryQueries.selectAll().executeAsList().first().id
        synchronizer.sync()
        val secondSyncId = database.entryQueries.selectAll().executeAsList().first().id

        assertThat(firstSyncId).isEqualTo(secondSyncId)
    }

    private fun buildInMemoryDatabase(): Database {
        val driver = JdbcSqliteDriver(IN_MEMORY)
        Database.Schema.create(driver)
        return DatabaseFactory.buildDatabase(driver)
    }
}
