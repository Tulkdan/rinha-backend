package com.example.features.people

import com.example.utils.LocalDateAsStringSerializer
import com.example.utils.UUIDAsStringSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.util.UUID

@Serializable
data class PersonResponse(
    @Serializable(UUIDAsStringSerializer::class)
    val id: UUID,
    val name: String,
    val nickname: String,
    @Serializable(LocalDateAsStringSerializer::class)
    val birthdate: LocalDate,
    val stack: List<String>?
)

@Serializable
data class PersonPayload(
    val name: String,
    val nickname: String,
    @Serializable(LocalDateAsStringSerializer::class)
    val birthdate: LocalDate,
    val stack: List<String>?
) {
    init {
        require(name.isNullOrBlank()) { "name cannot be empty" }
        require(nickname.isNullOrBlank()) { "name cannot be empty" }
    }
}
