package com.example.tables

import org.jetbrains.exposed.dao.id.LongIdTable

object People : LongIdTable() {
    val name = varchar("name", 100)
    val nickname = varchar("nickname", 32)
    val birthdate = varchar("birthdate", 50)
}