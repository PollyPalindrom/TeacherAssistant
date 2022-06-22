package com.example.teacherassistant.common

object StudentsParseManager {
    fun parseStudents(students: List<String>): String {
        var studentsString = ""
        for (student in students) {
            studentsString += "$student; "
        }
        return studentsString
    }
}