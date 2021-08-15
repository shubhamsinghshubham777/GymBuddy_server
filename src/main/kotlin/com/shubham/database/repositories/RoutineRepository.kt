package com.shubham.database.repositories

import com.shubham.database.DatabaseManager
import com.shubham.database.interfaces.RoutineRepositoryMethods
import com.shubham.database.tables.RoutineTable
import com.shubham.models.Result
import com.shubham.models.Routine
import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.entity.filter
import org.ktorm.entity.find
import org.ktorm.entity.sequenceOf
import org.ktorm.entity.toList
import org.postgresql.util.PSQLException

class RoutineRepository(
    private val db: Database
): RoutineRepositoryMethods {

    override suspend fun insertRoutine(routine: Routine): Result<Nothing> {
        return try {
            val result = db.insertAndGenerateKey(RoutineTable) {
                set(it.name, routine.name)
                set(it.days, routine.days)
                set(it.exerciseIds, routine.exerciseIds)
            } as? Int

            if (result != null) {
                Result.Success()
            } else {
                Result.FailureWithMsg("Couldn't insert routine!")
            }

        } catch (e: PSQLException) {
            Result.FailureWithMsg(e.localizedMessage)
        }
    }

    override suspend fun updateRoutine(routine: Routine): Result<Nothing> {
        return try {
            val result = db.update(RoutineTable) {
                set(it.name, routine.name)
                set(it.days, routine.days)
                set(it.exerciseIds, routine.exerciseIds)
                where { it.id eq routine.id }
            } as? Int

            when (result) {
                0 -> Result.FailureWithMsg("No routines were updated!")
                else -> Result.Success()
            }

        } catch (e: PSQLException) {
            Result.FailureWithMsg(e.localizedMessage)
        }
    }

    override suspend fun getAllRoutines(): Result<Routine> {
        return try {
            val listOfRoutines = db.sequenceOf(RoutineTable).toList().map { Routine(
                it.id,
                it.name,
                it.days,
                it.exerciseIds
            ) }
            Result.SuccessWithListOfData(listOfRoutines)
        } catch (e: PSQLException) {
            Result.FailureWithMsg(e.localizedMessage)
        }
    }

    override suspend fun deleteRoutine(id: Int): Result<Nothing> {
        return try {

            when (db.delete(RoutineTable) { it.id eq id }) {
                0 -> Result.FailureWithMsg("No routines were deleted!")
                else -> Result.Success()
            }
        } catch (e: PSQLException) {
            Result.FailureWithMsg(e.localizedMessage)
        }
    }

    override suspend fun deleteAllRoutines(): Result<Nothing> {
        return try {
            when (db.deleteAll(RoutineTable)) {
                0 -> Result.FailureWithMsg("No routines were deleted!")
                else -> Result.Success()
            }
        } catch (e: PSQLException) {
            Result.FailureWithMsg(e.localizedMessage)
        }
    }

    override suspend fun getAllUserRoutines(listOfRoutineIds: List<Int?>): Result<Routine> {
        return try {
            val listOfUserRoutines = db.sequenceOf(RoutineTable).toList().map {
                Routine(
                    it.id,
                    it.name,
                    it.days,
                    it.exerciseIds
                )
            }.filter {
                listOfRoutineIds.contains(it.id)
            }

            when (listOfUserRoutines.size) {
                0 -> Result.FailureWithMsg("No routines found!")
                else -> Result.SuccessWithListOfData(listOfUserRoutines)
            }
        } catch (e: PSQLException) {
            Result.FailureWithMsg(e.localizedMessage)
        }
    }
}