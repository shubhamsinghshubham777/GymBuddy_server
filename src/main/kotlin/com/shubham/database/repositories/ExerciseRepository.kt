package com.shubham.database.repositories

import com.shubham.database.DatabaseManager
import com.shubham.database.interfaces.ExerciseRepositoryMethods
import com.shubham.database.interfaces.MemberRepositoryMethods
import com.shubham.database.tables.ExerciseTable
import com.shubham.models.Exercise
import com.shubham.models.Result
import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.entity.firstOrNull
import org.ktorm.entity.sequenceOf
import org.ktorm.entity.toList
import org.postgresql.util.PSQLException

class ExerciseRepository(
    private val db: Database
): ExerciseRepositoryMethods {

    override suspend fun insertExercise(exercise: Exercise): Result<Nothing> {
        return try {
            val userId = db.insertAndGenerateKey(ExerciseTable) {
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
        val updatedRows = db.update(ExerciseTable) {
            set(it.body_part, exercise.body_part)
            set(it.sets, exercise.sets)
            set(it.reps, exercise.reps)
            set(it.video_link, exercise.video_link)
            where { it.id eq exercise.id }
        }
        return when(updatedRows) {
            0 -> Result.FailureWithMsg("No Rows Updated!")
            else -> Result.Success()
        }
    }

    override suspend fun findExercise(exerciseName: String): Result<Exercise> {
        val dbEntity = db.sequenceOf(ExerciseTable).firstOrNull { it.name eq exerciseName }
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

    override suspend fun getAllExercises(): Result<Exercise> {
        val list = db.sequenceOf(ExerciseTable).toList().map {
            Exercise(it.id, it.name, it.body_part, it.sets, it.reps, it.video_link, it.exercise_image)
        }
        return when {
            list.isNotEmpty() -> Result.SuccessWithListOfData(list)
            else -> Result.FailureWithMsg("No Data Found!")
        }
    }

    override suspend fun deleteExercise(id: Int): Result<Nothing> {
        return try {
            when (db.delete(ExerciseTable) { it.id eq id }) {
                0 -> Result.FailureWithMsg("No records were deleted!")
                else -> Result.Success()
            }
        } catch (e: PSQLException) {
            Result.FailureWithMsg(e.localizedMessage)
        }
    }

    override suspend fun deleteAllExercises(): Result<Nothing> {
        return try {
            db.deleteAll(ExerciseTable)
            Result.Success()
        } catch (e: PSQLException) {
            Result.FailureWithMsg(e.localizedMessage)
        }
    }
}