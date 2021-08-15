package com.shubham.utils.jwt

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm

class JWTService {

    private val issuer = "gymBuddy"
    private val jwtSecret = System.getenv("JWT_SECRET")
    private val algorithm = Algorithm.HMAC512(jwtSecret)

    val verifier = JWT
        .require(algorithm)
        .withIssuer(issuer)
        .build()

    fun generateToken(userEmail: String): String {
        return JWT.create()
            .withSubject("gymBuddyMember")
            .withIssuer(issuer)
            .withClaim("email", userEmail)
            .sign(algorithm)
    }

}