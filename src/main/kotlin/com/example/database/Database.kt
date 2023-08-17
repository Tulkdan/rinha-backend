package com.example.database

import com.example.tables.People
import com.example.tables.Stacks
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.config.*
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {
    fun init(config: ApplicationConfig) {
        val address = config.property("postgres.address").getString()
        val db = config.property("postgres.database").getString()
        val user = config.property("postgres.user").getString()
        val password = config.property("postgres.password").getString()
        
        val database = Database.connect(createHikariDataSource(address, db, user, password))
        transaction(database) {
            SchemaUtils.create(
                People,
                Stacks
            )
        }
    }
    
    private fun createHikariDataSource(
        address: String,
        db: String,
        user: String,
        password: String
    ) = HikariDataSource(HikariConfig().apply {
            driverClassName = "org.postgresql.Driver"
            jdbcUrl = "jdbc:postgresql://$address/$db"
            username = user
            maximumPoolSize = 3
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            setPassword(password)
            validate()
        })

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}