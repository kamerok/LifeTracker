package lifetracker.database

import android.content.Context
import com.squareup.sqldelight.android.AndroidSqliteDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class Data(context: Context) {

    private val database = Database(AndroidSqliteDriver(Database.Schema, context, "database.db"))

    suspend fun setData(
        properties: List<Property>,
        entries: List<Entry>,
        entryProperties: List<EntryProperty>
    ) = withContext(Dispatchers.IO) {
        database.transaction {
            database.propertyQueries.apply {
                clear()
                properties.forEach { insert(it) }
            }
            database.entryQueries.apply {
                clear()
                entries.forEach { insert(it) }
            }
            database.entryPropertyQueries.apply {
                clear()
                entryProperties.forEach { insert(it) }
            }
        }
    }

}
