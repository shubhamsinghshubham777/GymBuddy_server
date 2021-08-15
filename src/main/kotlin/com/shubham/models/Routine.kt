package com.shubham.models

import org.ktorm.support.postgresql.TextArray

data class Routine(
    val id: Int,
    val name: String,
    val days: TextArray?,
    val exerciseIds: TextArray?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Routine

        if (id != other.id) return false
        if (name != other.name) return false
        if (!days.contentEquals(other.days)) return false
        if (exerciseIds != null) {
            if (other.exerciseIds == null) return false
            if (!exerciseIds.contentEquals(other.exerciseIds)) return false
        } else if (other.exerciseIds != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + name.hashCode()
        result = 31 * result + days.hashCode()
        result = 31 * result + (exerciseIds?.contentHashCode() ?: 0)
        return result
    }
}
