package com.shubham.utils.routes

import com.shubham.database.repositories.MemberRepository
import com.shubham.models.GenericResponse
import com.shubham.models.Member
import com.shubham.models.Result
import com.shubham.models.MemberLoginRequest
import com.shubham.utils.jwt.JWTService
import com.shubham.utils.jwt.hashFromUserPassword
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.ktorm.database.Database

fun Route.memberRoutes(
    ktormDatabase: Database
) {

    val memberRepository = MemberRepository(ktormDatabase)
    val jwtService = JWTService()
    val encryptUserPassword = { userPassword: String -> hashFromUserPassword(userPassword) }

    get("/getAllMembers") {
        when (val result = memberRepository.getAllMembers()) {
            is Result.SuccessWithListOfData -> call.respond(HttpStatusCode.OK, result.data)
            is Result.FailureWithMsg -> call.respond(HttpStatusCode.ExpectationFailed, GenericResponse(result.msg))
        }
    }

    get("/getMember") {
        val userEmail = call.request.queryParameters["userEmail"]!!
        when (val result = memberRepository.findMember(userEmail)) {
            is Result.FailureWithMsg -> call.respond(HttpStatusCode.ExpectationFailed, GenericResponse(result.msg))
            is Result.SuccessWithData -> call.respond(HttpStatusCode.OK, result.data)
        }
    }

    post("/registerMember") {
        val memberReceived = call.receive<Member>()
        val memberToBeInserted = Member(
            null,
            memberReceived.name,
            memberReceived.email,
            memberReceived.phone,
            memberReceived.exercises,
            null,
            password = encryptUserPassword(memberReceived.password!!),
            token = jwtService.generateToken(memberReceived.email),
            userImage = memberReceived.userImage,
            routines = memberReceived.routines
        )

        when (val result = memberRepository.insertMember(memberToBeInserted)) {
            is Result.Success -> call.respond(HttpStatusCode.OK, GenericResponse(jwtService.generateToken(memberToBeInserted.email)))
            is Result.FailureWithMsg -> call.respond(HttpStatusCode.ExpectationFailed, GenericResponse(result.msg))
            else -> call.respond(HttpStatusCode.ExpectationFailed, GenericResponse("Couldn't insert member!"))
        }
    }

    post("/loginMember") {
        val receivedCredentials = call.receive<MemberLoginRequest>()

        when (val result = memberRepository.loginMember(receivedCredentials.email)) {
            is Result.FailureWithMsg -> call.respond(HttpStatusCode.NotFound, GenericResponse("No user found for these credentials!"))
            is Result.SuccessWithData -> {
                if (result.data.password == encryptUserPassword(receivedCredentials.password)) {

                    val foundUser = result.data

                    call.respond(HttpStatusCode.OK, foundUser.copy(password = null))
                } else {
                    println("Result data password is: ${result.data.password} \nand hashFromUserPassword is: ${hashFromUserPassword(receivedCredentials.password)}")
                    call.respond(HttpStatusCode.ExpectationFailed, GenericResponse("Incorrect password! Please try again."))
                }
            }
        }
    }

    delete("/deleteMember") {
        val userEmail = call.request.queryParameters["userEmail"]!!
        when (val result = memberRepository.deleteMember(userEmail)) {
            is Result.Success -> call.respond(HttpStatusCode.OK, GenericResponse("Successfully deleted user with email: $userEmail"))
            is Result.FailureWithMsg -> call.respond(HttpStatusCode.ExpectationFailed, GenericResponse(result.msg))
        }
    }

    delete("/deleteAllMembers") {
        when (val result = memberRepository.deleteAllMembers()) {
            is Result.Success -> call.respond(HttpStatusCode.OK, GenericResponse("All members deleted!"))
            is Result.FailureWithMsg -> call.respond(HttpStatusCode.ExpectationFailed, GenericResponse(result.msg))
        }
    }

    patch("/updateMember") {
        val receivedMember = call.receive<Member>()
        when (val result = memberRepository.updateMember(receivedMember)) {
            is Result.Success -> call.respond(HttpStatusCode.OK, GenericResponse("User updated successfully!"))
            is Result.FailureWithMsg -> call.respond(HttpStatusCode.ExpectationFailed, GenericResponse(result.msg))
        }
    }
}