package com.shubham.database.repositories

import com.shubham.database.DatabaseManager
import com.shubham.database.interfaces.MemberRepositoryMethods
import com.shubham.database.tables.MemberTable
import com.shubham.database.tables.MemberTable.id
import com.shubham.models.Member
import com.shubham.models.Result
import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.entity.firstOrNull
import org.ktorm.entity.sequenceOf
import org.ktorm.entity.toList
import org.postgresql.util.PSQLException

class MemberRepository(
    private val db: Database
): MemberRepositoryMethods {

    override suspend fun insertMember(member: Member): Result<Nothing> {
        return try {
            val result = db.insertAndGenerateKey(MemberTable) {
                set(it.email, member.email)
                set(it.exercises, member.exercises)
                set(it.name, member.name)
                set(it.phone, member.phone)
                set(it.password, member.password)
                set(it.token, member.token)
                set(it.userImage, member.userImage)
                set(it.routines, member.routines)
            } as? Int?

            when (result) {
                null -> Result.FailureWithMsg("Couldn't insert user!")
                else -> Result.Success()
            }
        } catch (e: PSQLException) {
            Result.FailureWithMsg(e.localizedMessage)
        }
    }

    override suspend fun updateMember(member: Member): Result<Nothing> {
        return try {

            val foundMemberResult = findMember(member.email)

            if (foundMemberResult is Result.SuccessWithData) {
                val foundMember = foundMemberResult.data
                val result = db.update(MemberTable) {

//                    if (foundMember.email != member.email && member.email.isNotBlank()) set(it.email, member.email)
                    if (!foundMember.exercises.contentEquals(member.exercises) && !member.exercises.isNullOrEmpty()) set(it.exercises, member.exercises)
                    if (foundMember.name != member.name && !member.name.isNullOrBlank()) set(it.name, member.name)
                    if (foundMember.phone != member.phone && member.phone > 0) set(it.phone, member.phone)
                    if (!foundMember.userImage.contentEquals(member.userImage) && member.userImage?.isNotEmpty() == true) set(it.userImage, member.userImage)
                    if (!foundMember.routines.contentEquals(member.routines) && !member.routines.isNullOrEmpty()) set(it.routines, member.routines)
                    if (!member.password.isNullOrBlank()) set(it.password, member.password)
//                set(it.token, member.token)
                    where { it.id eq id }

                } as? Int?

                return when (result) {
                    0 -> Result.FailureWithMsg("Couldn't update user!")
                    else -> Result.Success()
                }
            }

            Result.FailureWithMsg("No user found!")

        } catch (e: PSQLException) {
            Result.FailureWithMsg(e.localizedMessage)
        }
    }

    override suspend fun findMember(email: String): Result<Member> {
        val dbEntity = db.sequenceOf(MemberTable).firstOrNull { it.email eq email }
        return if (dbEntity == null) Result.FailureWithMsg("Member not found!") else Result.SuccessWithData(
            Member(
                dbEntity.id,
                dbEntity.name,
                dbEntity.email,
                dbEntity.phone,
                dbEntity.exercises,
                dbEntity.joinedOn,
                dbEntity.membershipType,
                null,
                dbEntity.token,
                dbEntity.userImage,
                dbEntity.routines
            )
        )
    }

    override suspend fun loginMember(email: String): Result<Member> {
        val dbEntity = db.sequenceOf(MemberTable).firstOrNull { it.email eq email }
        return if (dbEntity == null) Result.FailureWithMsg("Member not found!") else Result.SuccessWithData(
            Member(
                dbEntity.id,
                dbEntity.name,
                dbEntity.email,
                dbEntity.phone,
                dbEntity.exercises,
                dbEntity.joinedOn,
                dbEntity.membershipType,
                dbEntity.password,
                dbEntity.token,
                dbEntity.userImage,
                dbEntity.routines
            )
        )
    }

    override suspend fun getAllMembers(): Result<Member> {
        val list = db.sequenceOf(MemberTable).toList().map {
            Member(
                it.id,
                it.name,
                it.email,
                it.phone,
                it.exercises,
                it.joinedOn,
                it.membershipType,
                null,
                it.token,
                it.userImage,
                it.routines
            )
        }
        return when {
            list.isNotEmpty() -> Result.SuccessWithListOfData(list)
            else -> Result.FailureWithMsg("No Data Found!")
        }
    }

    override suspend fun deleteMember(email: String): Result<Nothing> {
        return try {
            when (db.delete(MemberTable) { it.email eq email }) {
                0 -> Result.FailureWithMsg("No user found with email: $email")
                else -> Result.Success()
            }
        } catch (e: PSQLException) {
            Result.FailureWithMsg(e.localizedMessage)
        }
    }

    override suspend fun deleteAllMembers(): Result<Nothing> {
        return try {
            db.deleteAll(MemberTable)
            Result.Success()
        } catch (e: PSQLException) {
            Result.FailureWithMsg(e.localizedMessage)
        }
    }
}