package com.example.student_module.ui.mainActivity

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.navigation.compose.rememberNavController
import com.example.common_module.common.*
import com.example.common_module.ui.mainActivity.MainActivityViewModel
import com.example.student_module.R
import com.example.student_module.di.StudentApplication
import com.example.student_module.ui.firebaseService.FirebaseService
import com.example.student_module.ui.navigation.NavGraphStudent
import com.example.common_module.ui.theme.AppTheme
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : AppCompatActivity(),
    SignInListener, DownloadPictureListener, PostToastListener {

    private lateinit var nextFragmentCallback: (String) -> Unit
    private var status = ""
    private val launcher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
        try {
            val account = task.getResult(ApiException::class.java)
            if (account != null) {
                account.idToken?.let { it1 -> firebaseAuth(it1) }
            }
        } catch (e: ApiException) {
            Toast.makeText(
                this,
                getString(R.string.api_problems),
                Toast.LENGTH_SHORT
            ).show()
        }
    }
    lateinit var viewModel: MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseService.sharedPref =
            this.getSharedPreferences(
                Constants.SHARED_PREF_NAME,
                Context.MODE_PRIVATE
            )
        setContent {
            AppTheme {
                Surface(color = MaterialTheme.colors.background) {
                    val navController = rememberNavController()
                    NavGraphStudent(
                        navController = navController,
                        signInListener = this,
                        downloadPictureListener = this
                    )
                    viewModel = daggerViewModel {
                        (application as StudentApplication).studentComponent.getMainActivityViewModel()
                    }
                    nextFragmentCallback =
                        { role ->
                            navController.popBackStack()
                            navController.navigate(Screen.GroupsScreen.route + "?${Constants.ROLE}=$role")
                        }
                }
            }
        }
    }

    private fun getClient(): GoogleSignInClient {
        val gso =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        return GoogleSignIn.getClient(this, gso)
    }

    override fun signIn(status: String) {
        try {
            this.status = status
            val signInClient = getClient()
            launcher.launch(signInClient.signInIntent)
        } catch (exception: ApiException) {
            Toast.makeText(
                this,
                getString(R.string.api_problems),
                Toast.LENGTH_SHORT
            ).show()
        }

    }

    private fun firebaseAuth(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        viewModel.getAuthResult(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                viewModel.checkRole(
                    nextFragmentCallback,
                    Constants.COLLECTION_FIRST_PATH,
                    status,
                    this
                ) { realStatus ->
                    saveUserInfo(realStatus)
                }
            } else {
                Toast.makeText(
                    this,
                    getString(R.string.signInFailed),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun saveUserInfo(role: String) {
        val userInfo = viewModel.getMapUserInfo()
        FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
            userInfo[Constants.TOKEN] = token
            if (role == Constants.TEACHER) userInfo[Constants.STATUS] =
                Constants.POSITIVE_STAT
            if (role == Constants.STUDENT) userInfo[Constants.STATUS] =
                Constants.NEGATIVE_STAT
            viewModel.setUserInfo(userInfo, Constants.COLLECTION_FIRST_PATH)
        }
    }

    override fun postToast(id: Int) {
        Toast.makeText(
            this,
            getString(id),
            Toast.LENGTH_LONG
        ).show()
    }

    override fun downloadPicture(uri: Uri) {
        val path = uri.lastPathSegment?.replace(":", "/")
        val request = DownloadManager.Request(uri)
            .setTitle(path)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, path)
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        (this.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager)
            .enqueue(request)
    }
}