package com.example.teacherassistant.common

import com.google.firebase.firestore.DocumentReference

object CheckRoleManager {
    fun checkRole(
        listener: OpenNextFragmentListener,
        documentReference: DocumentReference?
    ) {
        documentReference?.get()?.addOnSuccessListener {
            if (it.getString("isTeacher") == "1") {
                listener.openNextFragment("Teacher")
            }
            if (it.getString("isTeacher") == "0") {
                listener.openNextFragment("Student")
            }
        }
    }
}