package com.doubleclick.Repository

import com.doubleclick.data.table.MessageTable
import com.doubleclick.data.table.UserTable
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import java.net.URI

object DatabaseFactory {


    fun init() {
        Database.connect(hikari())
//         to create database in SQL pg admin
        transaction {
            SchemaUtils.create(UserTable)
            SchemaUtils.create(MessageTable)
        }
    }

    /*
    * to connect app with database by url
    * */
    private fun hikari(): HikariDataSource {
        val config = HikariConfig()
        // for Documentation https://github.com/pgjdbc/pgjdbc
        config.driverClassName = System.getenv("JDBC_DRIVER")//1
        config.jdbcUrl = System.getenv("DATABASE_URL")//2
        config.maximumPoolSize = 3;
        config.isAutoCommit = false
        config.transactionIsolation = "TRANSACTION_REPEATABLE_READ"
//        val uri = URI(System.getenv("DATABASE_URL"))
//        val username = uri.userInfo.split(":").toTypedArray()[0]
//        val password = uri.userInfo.split(":").toTypedArray()[1]
//        config.jdbcUrl =
//            "jdbc:postgresql://" + uri.host + ":" + uri.port + uri.path + "?sslmode=require" + "&user=$username&password=$password"
        config.validate()
        return HikariDataSource(config)

    }

    /*
    * to do query in coroutine block
    * */
    suspend fun <T> dbQuery(block: () -> T): T =
        withContext(Dispatchers.IO) {
            transaction { block() }
        }
}