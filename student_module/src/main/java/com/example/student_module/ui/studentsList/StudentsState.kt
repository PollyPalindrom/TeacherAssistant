package com.example.student_module.ui.studentsList

data class StudentsState(val students: List<String> = emptyList(), val error: String? = null)