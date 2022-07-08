package com.example.teacherassistant.ui.main.mainActivity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.navigation.compose.rememberNavController
import com.example.teacherassistant.R
import com.example.teacherassistant.common.Constants
import com.example.teacherassistant.common.PostToastListener
import com.example.teacherassistant.common.Screen
import com.example.teacherassistant.common.SignInListener
import com.example.teacherassistant.ui.main.firebaseService.FirebaseService
import com.example.teacherassistant.ui.main.navigation.NavGraph
import com.example.teacherassistant.ui.main.themes.AppTheme
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), PostToastListener,
    SignInListener {

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

        }
    }
    private val viewModel: MainActivityViewModel by viewModels<MainActivityViewModel>()

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
                    NavGraph(
                        navController = navController,
                        postToastListener = this,
                        signInListener = this
                    )
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
        this.status = status
        val signInClient = getClient()
        launcher.launch(signInClient.signInIntent)
    }

    private fun firebaseAuth(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        viewModel.getAuthResult(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                viewModel.checkRole(nextFragmentCallback, Constants.COLLECTION_FIRST_PATH)
                val userInfo = viewModel.getMapUserInfo()
                FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
                    userInfo[Constants.TOKEN] = token
                    if (status == Constants.TEACHER) userInfo[Constants.STATUS] =
                        Constants.POSITIVE_STAT
                    if (status == Constants.STUDENT) userInfo[Constants.STATUS] =
                        Constants.NEGATIVE_STAT
                    viewModel.setUserInfo(userInfo, Constants.COLLECTION_FIRST_PATH)
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

    override fun postToast(id: Int) {
        Toast.makeText(
            this,
            getString(id),
            Toast.LENGTH_LONG
        ).show()
    }
}