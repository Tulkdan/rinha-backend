package com.example.features.people

import com.example.tables.People
import com.example.tables.Stacks
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.routing.routing
import io.ktor.server.routing.post
import io.ktor.server.routing.get
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

fun Application.configurePeopleRoute() {
    routing { 
        post("/pessoas") {
            val person = call.receive<PersonPayload>()
            val personId = transaction { 
                val newPersonId = People.insertAndGetId {
                    it[People.name] = person.name
                    it[People.nickname] = person.nickname
                    it[People.birthdate] = person.birthdate
                }
                
                if (person.stack != null) {
                    person.stack.forEach { stack -> Stacks.insert {
                        it[Stacks.name] = stack
                        it[Stacks.person] = newPersonId
                    } }
                }
                
                return@transaction newPersonId.value
            }
            
            call.respond(HttpStatusCode.Created, personId.toString())
        }
        get("/pessoas/{id}") {
            val personId = call.parameters["id"]
            
            if (personId.isNullOrEmpty()) {
                call.respond(HttpStatusCode.NotFound)
                return@get
            }
            
            val userId = try {
                UUID.fromString(personId)
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }
            
            val result = transaction {
                val person = People.select {
                    People.id eq userId
                }.firstOrNull() ?: return@transaction null
                
                val stacks = Stacks.select { 
                    Stacks.person eq userId
                }.toList()
                
                return@transaction PersonResponse(
                    id = userId,
                    name = person[People.name],
                    nickname = person[People.nickname],
                    birthdate = person[People.birthdate],
                    stack = stacks.map { it[Stacks.name] }.ifEmpty { null }
                )
            }
            
           when (result) {
               null -> call.respond(HttpStatusCode.NotFound, "")
               else -> call.respond(HttpStatusCode.OK, result)
           }
        }
        get("/contagem-pessoas") {
            val qtd = transaction {
                People.selectAll().count()
            }
            
            call.respond(HttpStatusCode.OK, qtd)
        }
    }
}