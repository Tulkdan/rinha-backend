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
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

private const val MAX_USERS_QUANTITY = 50

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
        get("/pessoas") {
            val searchQuery = call.parameters["t"]
            
            if (searchQuery.isNullOrEmpty()) {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }
            
            val people = transaction {
                val peopleDB = People.select {
                    (People.name like ("%$searchQuery%")) or (People.nickname like ("%$searchQuery%"))
                }.toList()
                
                val people = peopleDB.map {person -> run {
                    val stacks = Stacks.select {
                        Stacks.person eq person[People.id]
                    }.toList()
                    
                    return@run PersonResponse(
                        id = person[People.id].value,
                        name = person[People.name],
                        nickname = person[People.nickname],
                        birthdate = person[People.birthdate],
                        stack = stacks.map { it[Stacks.name] }.ifEmpty { null }
                    )
                } }
                
                val amountToStillSearch = MAX_USERS_QUANTITY - people.size
                
                if (amountToStillSearch == 0) {
                    return@transaction people
                }
                
                val users = mutableListOf<PersonResponse>()
                users.addAll(people)
                
                val peopleIdAlreadyFound = people.map { it.id }
                
                val stackDB = Stacks.select {
                    (Stacks.name like ("%$searchQuery%")) and (Stacks.person notInList peopleIdAlreadyFound)
                }
                    .limit(amountToStillSearch)
                    .toList()
                
                val peopleStacks = stackDB.fold(mutableMapOf<UUID, MutableList<String>>()) { acc, stack -> run {
                    val stacksFromPersonInMap = acc[stack[Stacks.person].value]
                    if (stacksFromPersonInMap.isNullOrEmpty()) {
                        acc.put(stack[Stacks.person].value, mutableListOf(stack[Stacks.name]))
                        return@fold acc
                    }
                    
                    stacksFromPersonInMap.add(stack[Stacks.name])
                    acc.put(stack[Stacks.person].value, stacksFromPersonInMap)
                    return@fold acc
                } }
                    
                val peopleFromStacks = People.select {
                    People.id inList peopleStacks.keys
                }.toList().map { person -> run {
                    val stack = peopleStacks[person[People.id].value]

                    return@run PersonResponse(
                        id = person[People.id].value,
                        name = person[People.name],
                        nickname = person[People.nickname],
                        birthdate = person[People.birthdate],
                        stack = stack
                    )
                } }

                users.addAll(peopleFromStacks)
                return@transaction users
            }
            
            call.respond(HttpStatusCode.OK, people)
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