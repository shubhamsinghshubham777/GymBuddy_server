package com.shubham.plugins

import com.shubham.database.DatabaseManager
import com.shubham.database.repositories.MemberRepository
import com.shubham.models.Member
import com.shubham.models.Result
import com.shubham.utils.jwt.JWTService
import com.shubham.utils.routes.exerciseRoutes
import com.shubham.utils.routes.memberRoutes
import com.shubham.utils.routes.routineRoutes
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*

fun Application.configureRouting() {

    val ktormDatabase = DatabaseManager().ktormDatabase

    install(Locations) {
    }

    install(ContentNegotiation) {
        gson {
        }
    }

    install(Authentication) {

        val jwtService = JWTService()
        val memberRepository = MemberRepository(ktormDatabase)

        jwt {
            verifier(jwtService.verifier)
            realm = "gymBuddyRealm"
            validate {
                val payload = it.payload
                val email = payload.getClaim("email").asString()

                when (val result = memberRepository.findMember(email)) {
                    is Result.SuccessWithData -> result.data
                    else -> null
                }
            }
        }
    }

    routing {

        memberRoutes(ktormDatabase)
        exerciseRoutes(ktormDatabase)
        routineRoutes(ktormDatabase)

        get("/") {
            call.respond("Welcome to GymBuddy! \n\nIf you're seeing this message, this means your deployment has been successful! \n\nHave fun!")
        }

    }
}
