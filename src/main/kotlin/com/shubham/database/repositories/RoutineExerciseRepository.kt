package com.shubham.database.repositories

import com.shubham.database.interfaces.RoutineExerciseRepositoryMethods
import com.shubham.database.tables.RoutineExerciseTable
import com.shubham.models.Exercise
import com.shubham.models.Result
import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.entity.*
import org.postgresql.util.PSQLException

class RoutineExerciseRepository(
    private val db: Database
): RoutineExerciseRepositoryMethods {

    override suspend fun insertExercise(exercise: Exercise): Result<Nothing> {
        return try {
            val userId = db.insertAndGenerateKey(RoutineExerciseTable) {
                set(it.name, exercise.name)
                set(it.body_part, exercise.body_part)
                set(it.sets, exercise.sets)
                set(it.reps, exercise.reps)
                set(it.video_link, exercise.video_link)
                set(it.exercise_image, exercise.exercise_image)
            } as? Int?

            return if (userId != null) Result.Success() else Result.FailureWithMsg("Couldn't insert exercise!")
        } catch (e: PSQLException) {
            Result.FailureWithMsg(e.localizedMessage)
        }
    }

    override suspend fun updateExercise(exercise: Exercise): Result<Nothing> {
        return try {
            val updatedRows = db.update(RoutineExerciseTable) { table ->
                exercise.apply {
                    name?.let { set(table.name, it) }
                    body_part?.let { set(table.body_part, it) }
                    sets?.let { set(table.sets, it) }
                    reps?.let { set(table.reps, it) }
                    video_link?.let { set(table.video_link, it) }
                    exercise_image?.let { set(table.exercise_image, it) }
                    where { table.id eq id }
                }
            }
            when(updatedRows) {
                0 -> Result.FailureWithMsg("No Rows Updated!")
                else -> Result.Success()
            }
        } catch (e: Exception) {
            Result.FailureWithMsg(e.localizedMessage)
        }
    }

    override suspend fun findExercise(exerciseName: String): Result<Exercise> {
        val dbEntity = db.sequenceOf(RoutineExerciseTable).firstOrNull { it.name eq exerciseName }
        return if (dbEntity == null) Result.FailureWithMsg("Exercise not found!") else Result.SuccessWithData(
            Exercise(
                dbEntity.id,
                dbEntity.name,
                dbEntity.body_part,
                dbEntity.sets,
                dbEntity.reps,
                dbEntity.video_link,
                dbEntity.exercise_image,
            )
        )
    }

    override suspend fun getAllExercisesWithIdList(listOfExerciseIds: List<Int>): Result<Exercise> {
        val list = db.sequenceOf(RoutineExerciseTable).toList().map {
            Exercise(it.id, it.name, it.body_part, it.sets, it.reps, it.video_link, it.exercise_image)
        }.filter { it.id in listOfExerciseIds.toIntArray() }.sortedBy { it.id }
        return when {
            list.isNotEmpty() -> Result.SuccessWithListOfData(list)
            else -> Result.FailureWithMsg("No Data Found!")
        }
    }

    override suspend fun deleteExercise(id: Int): Result<Nothing> {
        return try {
            when (db.delete(RoutineExerciseTable) { it.id eq id }) {
                0 -> Result.FailureWithMsg("No records were deleted!")
                else -> Result.Success()
            }
        } catch (e: PSQLException) {
            Result.FailureWithMsg(e.localizedMessage)
        }
    }

    override suspend fun deleteAllExercises(): Result<Nothing> {
        return try {
            db.deleteAll(RoutineExerciseTable)
            Result.Success()
        } catch (e: PSQLException) {
            Result.FailureWithMsg(e.localizedMessage)
        }
    }
}