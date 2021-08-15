package com.shubham.database.interfaces

import com.shubham.models.Member
import com.shubham.models.Result

interface MemberRepositoryMethods {

    suspend fun insertMember(member: Member): Result<Nothing>
    suspend fun updateMember(member: Member): Result<Nothing>
    suspend fun findMember(email: String): Result<Member>
    suspend fun loginMember(email: String): Result<Member>
    suspend fun getAllMembers(): Result<Member>
    suspend fun deleteMember(email: String): Result<Nothing>
    suspend fun deleteAllMembers(): Result<Nothing>

}