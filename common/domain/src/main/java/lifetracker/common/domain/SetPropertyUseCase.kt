package lifetracker.common.domain

import lifetracker.common.database.Data


class SetPropertyUseCase(
    private val database: Data,
    private val spreadsheetService: SpreadsheetService
) {

    suspend fun set(entryId: String, propertyId: String, value: Boolean?) {
        val rowNumber =
            database.getEntry(entryId).position + 1 /*first row*/ + 1 /*indexes start from zero but table not*/
        val columnNumber = database.getProperty(propertyId).position + 1
        val cellValue = (value?.let { if (it) "Y" else "N" }) ?: ""
        spreadsheetService.setCell(rowNumber, columnNumber, cellValue)
    }

}
