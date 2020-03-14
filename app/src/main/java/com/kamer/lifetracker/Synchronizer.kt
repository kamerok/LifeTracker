package com.kamer.lifetracker


class Synchronizer(
    private val updateDataUseCase: UpdateDataUseCase,
    private val service: Service
) {

    suspend fun sync() = updateDataUseCase.saveData(service.getData())

}
