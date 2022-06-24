package com.example.teacherassistant.common

import com.google.firebase.firestore.DocumentReference

object CheckRoleManager {
    fun checkRole(
        openNextFragment: (role:String)->Unit,
        documentReference: DocumentReference?
    ) {
        documentReference?.get()?.addOnSuccessListener {
            if (it.getString("isTeacher") == "1") {
                openNextFragment("Teacher")
            }
            if (it.getString("isTeacher") == "0") {
                openNextFragment("Student")
            }
        }
    }
}