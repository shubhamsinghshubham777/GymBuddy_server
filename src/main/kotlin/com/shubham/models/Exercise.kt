package com.shubham.models

data class Exercise(
    val id: Int,
    val name: String,
    val body_part: String,
    val sets: Int,
    val reps: Int,
    val video_link: String,
    val exercise_image: String
)