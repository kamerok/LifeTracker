package lifetracker.common.domain


class Synchronizer(
    private val updateDataUseCase: UpdateDataUseCase,
    private val service: SpreadsheetService
) {

    suspend fun sync() = updateDataUseCase.saveData(service.getData())

}
