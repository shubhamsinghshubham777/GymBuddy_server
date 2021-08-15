package com.shubham

import io.ktor.server.engine.*
import io.ktor.server.cio.*
import com.shubham.plugins.*
import java.net.URI

fun main() {

    val uri = URI(System.getenv("DATABASE_URL"))

    embeddedServer(CIO, port = System.getenv("PORT").toInt(), host = uri.host) {
        configureRouting()
        configureMonitoring()
    }.start(wait = true)
}
