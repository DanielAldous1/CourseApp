package com.example.courseapp

import java.util.UUID

data class Course(
    val id: String = UUID.randomUUID().toString(),
    val department: String,
    val number: String,
    val location: String
) {
    val name: String get() = "${department.trim()} ${number.trim()}"
}
