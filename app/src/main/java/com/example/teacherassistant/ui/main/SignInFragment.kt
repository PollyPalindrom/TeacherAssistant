package com.example.teacherassistant.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.teacherassistant.R
import com.example.teacherassistant.common.OpenNextFragmentListener
import com.example.teacherassistant.databinding.SignInFragmentBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignInFragment : Fragment(), OpenNextFragmentListener {

    private lateinit var binding: SignInFragmentBinding
    private lateinit var launcher: ActivityResultLauncher<Intent>

    private val viewModel by viewModels<SignInViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = SignInFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    account.idToken?.let { it1 -> firebaseAuth(it1) }
                }
            } catch (e: ApiException) {

            }
        }
        binding.SignInButton.setOnClickListener {
            signIn()
        }

        val spinnerAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            resources.getStringArray(R.array.spinnerArray)
        )
        binding.autoCompleteTextView.apply {
            setAdapter(spinnerAdapter)
            threshold = 2
            onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    binding.autoCompleteTextView.showDropDown()
                }
            }
        }
        viewModel.checkRole(this)
    }

    private fun getClient(): GoogleSignInClient {
        val gso =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        return GoogleSignIn.getClient(requireContext(), gso)
    }

    private fun signIn() {
        val signInClient = getClient()
        launcher.launch(signInClient.signInIntent)
    }

    private fun firebaseAuth(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        viewModel.getAuthResult(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                viewModel.checkRole(this)
                val userInfo = viewModel.getMapUserInfo()
                if (binding.autoCompleteTextView.text.toString().isNotEmpty()) {
                    if (binding.autoCompleteTextView.text.toString() == "Teacher") userInfo["isTeacher"] =
                        "1"
                    if (binding.autoCompleteTextView.text.toString() == "Student") userInfo["isTeacher"] =
                        "0"
                }
                viewModel.setUserInfo(userInfo, "User")
            } else {

            }
        }
    }

    override fun openNextFragment(path: String) {
        if (viewModel.checkState()) {
            val bundle = Bundle()
            bundle.putString("Role", path)
            findNavController().navigate(R.id.mainFragment, bundle)
        }
    }
}