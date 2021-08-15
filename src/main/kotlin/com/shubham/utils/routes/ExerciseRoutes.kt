package com.shubham.utils.routes

import com.shubham.database.repositories.ExerciseRepository
import com.shubham.models.Exercise
import com.shubham.models.GenericResponse
import com.shubham.models.Result
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.ktorm.database.Database

fun Route.exerciseRoutes(
    ktormDatabase: Database
) {

    val exerciseRepository = ExerciseRepository(ktormDatabase)

    get("/getAllExercises") {
        when (val result = exerciseRepository.getAllExercises()) {
            is Result.SuccessWithListOfData -> call.respond(result.data)
            is Result.FailureWithMsg -> call.respond(HttpStatusCode.ExpectationFailed, GenericResponse(result.msg))
            else -> call.respond(HttpStatusCode.ExpectationFailed, GenericResponse("Couldn't get exercises!"))
        }
    }

    post("/postExercise") {
        val exerciseReceived = call.receive<Exercise>()

        when (val result = exerciseRepository.insertExercise(exerciseReceived)) {
            is Result.Success -> {
                call.respond(HttpStatusCode.OK, GenericResponse("Exercise inserted successfully!"))
            }
            is Result.FailureWithMsg -> {
                call.respond(
                    HttpStatusCode.ExpectationFailed, when {
                    result.msg.contains("already exists") -> GenericResponse("This exercise already exists inside the DB!")
                    else -> GenericResponse(result.msg)
                })
            }
        }
    }

    delete("/deleteAllExercises") {
        when (val result = exerciseRepository.deleteAllExercises()) {
            is Result.Success -> {
                call.respond(HttpStatusCode.OK, GenericResponse("All Exercises deleted successfully!"))
            }
            is Result.FailureWithMsg -> {
                call.respond(HttpStatusCode.ExpectationFailed, GenericResponse(result.msg))
            }
        }
    }
}