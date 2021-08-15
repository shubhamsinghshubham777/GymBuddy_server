package com.shubham.models

import io.ktor.auth.*
import org.ktorm.support.postgresql.TextArray
import java.time.LocalDate

data class Member(
    val id: Int?,
    val name: String?,
    val email: String,
    val phone: Long,
    val exercises: TextArray?,
    val joinedOn: LocalDate?,
    val membershipType: MembershipType? = MembershipType.STANDARD_MEMBER,
    val password: String?,
    val token: String,
    val userImage: ByteArray?,
    val routines: TextArray?
): Principal {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Member

        if (id != other.id) return false
        if (name != other.name) return false
        if (email != other.email) return false
        if (phone != other.phone) return false
        if (!exercises.contentEquals(other.exercises)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result!! + name.hashCode()
        result = 31 * result + email.hashCode()
        result = 31 * result + phone.hashCode()
        result = 31 * result + exercises.contentHashCode()
        return result
    }
}

enum class MembershipType {
    STANDARD_MEMBER,
    GOLD_MEMBER,
    PLATINUM_MEMBER
}
