package com.example.features.people

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class Person(
    val id: Int,
    val name: String,
    val nickname: String,
    @Contextual
    val birthdate: LocalDate,
    val stack: List<String>?
)
