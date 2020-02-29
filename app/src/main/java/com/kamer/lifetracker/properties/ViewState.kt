package com.kamer.lifetracker.properties


data class ViewState(
    val properties: List<UiProperty>
)

data class UiProperty(
    val id: String,
    val name: String
)
