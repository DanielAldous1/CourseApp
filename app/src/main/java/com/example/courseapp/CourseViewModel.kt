package com.example.courseapp

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class CourseViewModel : ViewModel() {

    private val _courses = MutableStateFlow(
        listOf(
            Course(department = "CS", number = "4530", location = "WEB 105"), Course(department = "MATH", number = "1210", location = "WEB 104")
        )
    )
    val courses: StateFlow<List<Course>> = _courses

    private val _selected = MutableStateFlow<Course?>(null)
    val selected: StateFlow<Course?> = _selected

    fun select(course: Course) {
        _selected.value = course
    }

    fun clearSelection() {
        _selected.value = null
    }

    fun addCourse(department: String, number: String, location: String) {
        val dept = department.trim()
        val num = number.trim()
        val loc = location.trim()
        if (dept.isBlank() || num.isBlank() || loc.isBlank()) return

        _courses.update { it + Course(department = dept, number = num, location = loc) }
    }

    fun updateCourse(id: String, department: String, number: String, location: String) {
        val dept = department.trim()
        val num = number.trim()
        val loc = location.trim()
        if (dept.isBlank() || num.isBlank() || loc.isBlank()) return

        _courses.update { list ->
            list.map { c ->
                if (c.id == id) c.copy(department = dept, number = num, location = loc)
                else c
            }
        }

        _selected.value = _courses.value.firstOrNull { it.id == id }
    }

    fun deleteCourse(course: Course) {
        _courses.update { it.filterNot { c -> c.id == course.id } }
        if (_selected.value?.id == course.id) _selected.value = null
    }
}
