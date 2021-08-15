package com.shubham.utils.routes

import com.shubham.database.repositories.RoutineRepository
import com.shubham.models.GenericResponse
import com.shubham.models.Result
import com.shubham.models.Routine
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.ktorm.database.Database
import org.ktorm.support.postgresql.TextArray

fun Route.routineRoutes(
    ktormDatabase: Database
) {

    val routineRepository = RoutineRepository(ktormDatabase)

    authenticate {

        route("/member") {

            get("/getAllRoutines") {
                when (val result = routineRepository.getAllRoutines()) {
                    is Result.SuccessWithListOfData -> {
                        if (result.data.isEmpty()) {
                            call.respond(HttpStatusCode.NotFound, GenericResponse("No routines found!"))
                        } else {
                            call.respond(HttpStatusCode.OK, result.data)
                        }
                    }
                    is Result.FailureWithMsg -> call.respond(HttpStatusCode.ExpectationFailed, GenericResponse(result.msg))
                }
            }

            post("/insertRoutine") {

                val receivedRoutine = call.receive<Routine>()

                when (val result = routineRepository.insertRoutine(receivedRoutine)) {
                    is Result.Success -> call.respond(HttpStatusCode.OK, GenericResponse("Routine was inserted successfully!"))
                    is Result.FailureWithMsg -> call.respond(HttpStatusCode.ExpectationFailed, GenericResponse(result.msg))
                }
            }

            delete("/deleteRoutine") {
                val routineId = call.request.queryParameters["routineId"]?.toIntOrNull()
                routineId?.let { nonNullRoutineId ->
                    when (val result = routineRepository.deleteRoutine(nonNullRoutineId)) {
                        is Result.Success -> call.respond(HttpStatusCode.OK, GenericResponse("Routine successfully deleted!"))
                        is Result.FailureWithMsg -> call.respond(HttpStatusCode.ExpectationFailed, GenericResponse(result.msg))
                    }
                }

            }

            patch("/updateRoutine") {
                val receivedRoutine = call.receive<Routine>()

                when (val result = routineRepository.updateRoutine(receivedRoutine)) {
                    is Result.Success -> call.respond(HttpStatusCode.OK, GenericResponse("Updated routine successfully!"))
                    is Result.FailureWithMsg -> call.respond(HttpStatusCode.ExpectationFailed, GenericResponse(result.msg))
                }
            }

            get("/getUserRoutines") {
                val userRoutineIds = call.receive<TextArray>().map { it?.toIntOrNull() }
                when (val result = routineRepository.getAllUserRoutines(userRoutineIds)) {
                    is Result.SuccessWithListOfData -> call.respond(HttpStatusCode.OK, result.data)
                    is Result.FailureWithMsg -> call.respond(HttpStatusCode.NotFound, GenericResponse(result.msg))
                }
            }
        }
    }

    delete("/deleteAllRoutines") {
        when (val result = routineRepository.deleteAllRoutines()) {
            is Result.Success -> call.respond(HttpStatusCode.OK, GenericResponse("Deleted all routines successfully!"))
            is Result.FailureWithMsg -> call.respond(HttpStatusCode.ExpectationFailed, GenericResponse(result.msg))
        }
    }

}