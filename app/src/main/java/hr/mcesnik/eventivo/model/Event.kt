package hr.mcesnik.eventivo.model

import java.util.Date

data class Event(
    val title: String = "",
    val date: Date? = null,
    val clothing: String = "",
    val drink: String = "",
    val music: String = "",
    val image: String = ""
)