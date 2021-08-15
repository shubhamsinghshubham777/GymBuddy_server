package com.shubham.database.tables

import org.ktorm.entity.Entity
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.text
import org.ktorm.support.postgresql.TextArray
import org.ktorm.support.postgresql.textArray

object RoutineTable: Table<RoutineEntity>("routine") {

    val id = int("id").primaryKey().bindTo { it.id }
    val name = text("name").bindTo { it.name }
    val days = textArray("days").bindTo { it.days }
    val exerciseIds = textArray("exerciseIds").bindTo { it.exerciseIds }

}

interface RoutineEntity: Entity<RoutineEntity> {

    companion object : Entity.Factory<RoutineEntity>()

    val id: Int
    val name: String
    val days: TextArray?
    val exerciseIds: TextArray?

}