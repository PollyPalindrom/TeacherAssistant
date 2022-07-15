package com.example.teacherassistant.common

import com.example.teacherassistant.R
import com.google.firebase.firestore.DocumentReference

object CheckRoleManager {
    fun checkRole(
        openNextFragment: (role: String) -> Unit,
        documentReference: DocumentReference?
    ) {
        documentReference?.get()?.addOnSuccessListener {
            if (it.getString(Constants.STATUS) == Constants.POSITIVE_STAT) {
                openNextFragment(Constants.TEACHER)
            }
            if (it.getString(Constants.STATUS) == Constants.NEGATIVE_STAT) {
                openNextFragment(Constants.STUDENT)
            }
        }
    }

    fun checkRole(
        openNextFragment: (role: String) -> Unit,
        documentReference: DocumentReference?,
        status: String,
        postToastListener: PostToastListener,
        saveUserInfo: (realRole: String) -> Unit
    ) {
        documentReference?.get()?.addOnSuccessListener {
            if (it.getString(Constants.STATUS) == Constants.POSITIVE_STAT && status == Constants.TEACHER) {
                openNextFragment(Constants.TEACHER)
                saveUserInfo(Constants.TEACHER)
            }
            if (it.getString(Constants.STATUS) == Constants.NEGATIVE_STAT && status == Constants.STUDENT) {
                openNextFragment(Constants.STUDENT)
                saveUserInfo(Constants.STUDENT)
            } else if (it.getString(Constants.STATUS) == Constants.POSITIVE_STAT) {
                postToastListener.postToast(R.string.user_already_has_another_role_error)
                openNextFragment(Constants.TEACHER)
            } else if (it.getString(Constants.STATUS) == Constants.NEGATIVE_STAT) {
                postToastListener.postToast(R.string.user_already_has_another_role_error)
                openNextFragment(Constants.STUDENT)
            }
        }
    }
}