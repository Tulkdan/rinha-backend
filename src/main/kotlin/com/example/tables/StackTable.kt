package com.example.tables

import org.jetbrains.exposed.dao.id.LongIdTable

object Stacks : LongIdTable() {
    val name = text("name").index()
    val person = reference("person", People).index()
}