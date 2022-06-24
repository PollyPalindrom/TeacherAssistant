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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.teacherassistant.R
import com.example.teacherassistant.common.*
import com.example.teacherassistant.ui.main.entryFragment.EntryImage
import com.example.teacherassistant.ui.main.firebaseService.FirebaseService
import com.example.teacherassistant.ui.main.mainFragment.MainScreen
import com.example.teacherassistant.ui.main.notesFragment.NotesScreen
import com.example.teacherassistant.ui.main.signInFragment.SignInScreen
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
            Surface(color = MaterialTheme.colors.background) {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = Screen.EntryScreen.route
                ) {
                    composable(
                        route = Screen.EntryScreen.route
                    ) {
                        EntryImage()
                    }
                    composable(route = Screen.SignInScreen.route) {
                        SignInScreen(listener = getSignInListener())
                    }
                    composable(
                        route = Screen.NotesScreen.route +
                                "?${Constants.ROLE}={${Constants.ROLE}}&${Constants.GROUP_ID}={${Constants.GROUP_ID}}",
                        arguments = listOf(
                            navArgument(Constants.ROLE) {
                                defaultValue = Constants.STUDENT
                                type = NavType.StringType
                            },
                            navArgument(Constants.GROUP_ID) {
                                defaultValue = Constants.GROUP_ID
                                type = NavType.StringType
                            })
                    ) {
                        NotesScreen(
                            listener = getPostListener(),
                            role = it.arguments?.getString(Constants.ROLE),
                            groupId = it.arguments?.getString(Constants.GROUP_ID)
                        )
                    }
                    composable(
                        route = Screen.GroupsScreen.route +
                                "?${Constants.ROLE}={${Constants.ROLE}}",
                        arguments = listOf(
                            navArgument(Constants.ROLE) {
                                defaultValue = Constants.STUDENT
                                type = NavType.StringType
                            })
                    ) {
                        MainScreen(
                            navController = navController,
                            listener = getPostListener(),
                            role = it.arguments?.getString(Constants.ROLE)
                        )
                    }
                }
                if (viewModel.getUserState()) {
                    nextFragmentCallback =
                        { role -> navController.navigate(Screen.GroupsScreen.route + "?Role=$role") }
                    viewModel.checkRole(
                        nextFragmentCallback,
                        Constants.COLLECTION_FIRST_PATH
                    )
                } else {
                    navController.navigate(Screen.SignInScreen.route)
                }

            }
        }
    }

    private fun getPostListener(): PostToastListener = this
    private fun getSignInListener(): SignInListener = this

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