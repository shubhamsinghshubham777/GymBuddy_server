package com.shubham.database.interfaces

import com.shubham.models.Exercise
import com.shubham.models.Result

interface RoutineExerciseRepositoryMethods {

    suspend fun insertExercise(exercise: Exercise): Result<Nothing>
    suspend fun updateExercise(exercise: Exercise): Result<Nothing>
    suspend fun findExercise(exerciseName: String): Result<Exercise>
    suspend fun getAllExercisesWithIdList(listOfExerciseIds: List<Int>): Result<Exercise>
    suspend fun deleteExercise(id: Int): Result<Nothing>
    suspend fun deleteAllExercises(): Result<Nothing>
    
}