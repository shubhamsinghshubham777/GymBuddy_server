package com.shubham.utils.routes

import com.shubham.database.repositories.RoutineExerciseRepository
import com.shubham.models.Exercise
import com.shubham.models.GenericResponse
import com.shubham.models.Result
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.ktorm.database.Database

fun Route.routineExerciseRoutes(
    ktormDatabase: Database
) {

    val routineExerciseRepository = RoutineExerciseRepository(ktormDatabase)

    authenticate {
        route("/routineExercise") {
            get("/getAllExercises") {

                val listOfIds = call.receive<List<Int>>()

                when (val result = routineExerciseRepository.getAllExercisesWithIdList(listOfIds)) {
                    is Result.SuccessWithListOfData -> call.respond(result.data)
                    is Result.FailureWithMsg -> call.respond(HttpStatusCode.ExpectationFailed, GenericResponse(result.msg))
                    else -> call.respond(HttpStatusCode.ExpectationFailed, GenericResponse("Couldn't get exercises!"))
                }
            }

            post("/postExercise") {
                val exerciseReceived = call.receive<Exercise>()

                when (val result = routineExerciseRepository.insertExercise(exerciseReceived)) {
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

            patch("/updateExercise") {
                val exerciseReceived = call.receive<Exercise>()

                when (val result = routineExerciseRepository.updateExercise(exerciseReceived)) {
                    is Result.Success -> {
                        call.respond(HttpStatusCode.OK, GenericResponse("Exercise updated successfully!"))
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
                when (val result = routineExerciseRepository.deleteAllExercises()) {
                    is Result.Success -> {
                        call.respond(HttpStatusCode.OK, GenericResponse("All Exercises deleted successfully!"))
                    }
                    is Result.FailureWithMsg -> {
                        call.respond(HttpStatusCode.ExpectationFailed, GenericResponse(result.msg))
                    }
                }
            }

            delete("/deleteExercise") {

                val exerciseId = call.request.queryParameters["exerciseId"]?.toIntOrNull()

                exerciseId?.let { exerciseIdNotNull ->
                    when (val result = routineExerciseRepository.deleteExercise(exerciseIdNotNull)) {
                        is Result.Success -> {
                            call.respond(HttpStatusCode.OK, GenericResponse("Exercise deleted successfully!"))
                        }
                        is Result.FailureWithMsg -> {
                            call.respond(HttpStatusCode.ExpectationFailed, GenericResponse(result.msg))
                        }
                    }
                } ?: call.respond(HttpStatusCode.ExpectationFailed, GenericResponse("Entered ID was invalid. Please enter a valid ID!"))
            }
        }
    }
}