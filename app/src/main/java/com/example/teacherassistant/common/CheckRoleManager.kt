package com.example.teacherassistant.common

import com.example.teacherassistant.R
import com.google.firebase.firestore.DocumentReference

object CheckRoleManager {
    fun checkRole(
        openNextFragment: (role: String) -> Unit,
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

    fun checkRole(
        openNextFragment: (role: String) -> Unit,
        documentReference: DocumentReference?,
        status: String,
        postToastListener: PostToastListener,
        saveUserInfo: () -> Unit
    ) {
        documentReference?.get()?.addOnSuccessListener {
            if (it.getString("isTeacher") == "1" && status == Constants.TEACHER) {
                openNextFragment("Teacher")
                saveUserInfo()
            }
            if (it.getString("isTeacher") == "0" && status == Constants.STUDENT) {
                openNextFragment("Student")
                saveUserInfo()
            } else if (it.getString("isTeacher") == "1") {
                postToastListener.postToast(R.string.user_already_has_another_role_error)
                openNextFragment("Teacher")
            } else if (it.getString("isTeacher") == "0") {
                postToastListener.postToast(R.string.user_already_has_another_role_error)
                openNextFragment("Student")
            }
        }
    }
}