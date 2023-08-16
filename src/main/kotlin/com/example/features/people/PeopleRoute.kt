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
import org.jetbrains.exposed.sql.transactions.transaction

fun Application.configurePeopleRoute() {
    routing { 
        post("/pessoas") {
            val person = call.receive<Person>()
            val personId = transaction { 
                val newPersonId = People.insertAndGetId {
                    it[People.name] = person.name
                    it[People.nickname] = person.nickname
                    it[People.birthdate] = person.birthdate.toString()
                }
                
                if (person.stack != null) {
                    person.stack.forEach { stack -> Stacks.insert {
                        it[Stacks.name] = stack
                        it[Stacks.person] = newPersonId
                    } }
                }
                
                return@transaction newPersonId.value
            }
            
            call.respond(HttpStatusCode.Created, personId)
        }
        get("/pessoas/{id}") {}
        get("/contagem-pessoas") {}
    }
}