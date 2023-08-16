package com.example

import com.example.database.DatabaseFactory
import com.example.features.people.configurePeopleRoute
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.netty.EngineMain
import io.ktor.serialization.kotlinx.json.*

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
    DatabaseFactory.init(environment.config)
    
    install(ContentNegotiation) { json() }
    
    configurePeopleRoute()
    
    // configureDatabases()
}
