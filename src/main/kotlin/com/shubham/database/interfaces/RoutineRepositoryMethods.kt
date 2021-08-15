package com.shubham.database.interfaces

import com.shubham.models.Result
import com.shubham.models.Routine

interface RoutineRepositoryMethods {

    suspend fun insertRoutine(routine: Routine): Result<Nothing>
    suspend fun updateRoutine(routine: Routine): Result<Nothing>
    suspend fun getAllRoutines(): Result<Routine>
    suspend fun deleteRoutine(id: Int): Result<Nothing>
    suspend fun deleteAllRoutines(): Result<Nothing>
    suspend fun getAllUserRoutines(listOfRoutineIds: List<Int?>): Result<Routine>

}