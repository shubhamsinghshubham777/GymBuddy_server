package com.shubham

import io.ktor.server.engine.*
import io.ktor.server.cio.*
import com.shubham.plugins.*

fun main() {
    embeddedServer(CIO, port = 8080, host = "0.0.0.0") {
        configureRouting()
        configureMonitoring()
    }.start(wait = true)
}
