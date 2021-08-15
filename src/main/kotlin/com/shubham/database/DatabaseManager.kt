package com.shubham.database

import org.ktorm.database.Database
import java.net.URI

class DatabaseManager {

    private val uri = URI(System.getenv("DATABASE_URL"))
//    private val hostname = "localhost"
//    private val databaseName = "gymbuddy"
//    private val username = System.getenv("DB_USERNAME")
    private val username = uri.userInfo.split(":").toTypedArray()[0]
//    private val password = System.getenv("DB_PASSWORD")
    private val password = uri.userInfo.split(":").toTypedArray()[1]

    val ktormDatabase: Database

    init {
//        val jdbcUrl = "jdbc:postgresql://$hostname:5432/$databaseName?user=$username&password=$password&useSSL=false"
        val jdbcUrl = "jdbc:postgresql://${uri.host}:${uri.port}${uri.path}?user=$username&password=$password&sslmode=disable"
        println("You are connecting to this url: $jdbcUrl")
        ktormDatabase = Database.connect(jdbcUrl)
    }

}