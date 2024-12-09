package edu.uw.ischool.jtay25.digitalrecipebook

data class Recipe(
    val id : String = "",
    val title: String = "",
    val chef: String = "",
    val duration: String = "",
    val category: String = "",
    var tag: Boolean = false,
    val ingredients: List<String> = listOf(),
    val instructions: List<String> = listOf()
)