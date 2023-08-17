package com.example.tables

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.javatime.date

object People : UUIDTable() {
    val name = varchar("name", 100)
    val nickname = varchar("nickname", 32)
    val birthdate = date("birthdate")
}