package com.shubham.database.tables

import com.shubham.database.tables.ExerciseTable.bindTo
import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.text

object RoutineExerciseTable: Table<RoutineExerciseEntity>("routine_exercise") {
    val id = int("id").primaryKey().bindTo { it.id }
    val name = text("name").bindTo { it.name }
    val body_part = text("body_part").bindTo { it.body_part }
    val sets = int("sets").bindTo { it.sets }
    val reps = int("reps").bindTo { it.reps }
    val video_link = text("video_link").bindTo { it.video_link }
    val exercise_image = text("exercise_image").bindTo { it.exercise_image }
    val about = text("about").bindTo { it.about }
}

interface RoutineExerciseEntity: Entity<RoutineExerciseEntity> {
    companion object : Entity.Factory<RoutineExerciseEntity>()

    val id: Int
    val name: String
    val body_part: String
    val sets: Int
    val reps: Int
    val video_link: String
    val exercise_image: String
    val about: String
}