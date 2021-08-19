package com.shubham.database.tables

import com.shubham.models.Gender
import com.shubham.models.MembershipType
import org.ktorm.entity.Entity
import org.ktorm.schema.*
import org.ktorm.support.postgresql.TextArray
import org.ktorm.support.postgresql.textArray
import java.time.LocalDate

object MemberTable: Table<MemberEntity>("member") {
    val id = int("id").primaryKey().bindTo { it.id }
    val name = text("name").bindTo { it.name }
    val email = text("email").bindTo { it.email }
    val phone = long("phone").bindTo { it.phone }
    val exercises = textArray("exercises").bindTo { it.exercises }
    val joinedOn = date("joinedOn").bindTo { it.joinedOn }
    val membershipType = enum<MembershipType>("membershipType").bindTo { it.membershipType }
    val password = text("password").bindTo { it.password }
    val token = text("token").bindTo { it.token }
    val userImage = bytes("userImage").bindTo { it.userImage }
    val routines = textArray("routines").bindTo { it.routines }
    val gender = enum<Gender>("gender").bindTo { it.gender }
}

interface MemberEntity: Entity<MemberEntity> {
    companion object : Entity.Factory<MemberEntity>()

    val id: Int
    val name: String
    val email: String
    val phone: Long
    val exercises: TextArray
    val joinedOn: LocalDate
    val membershipType: MembershipType?
    val password: String?
    val token: String
    val userImage: ByteArray?
    val routines: TextArray?
    val gender: Gender?
}