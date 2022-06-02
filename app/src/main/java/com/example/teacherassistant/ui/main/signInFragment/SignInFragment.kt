package com.example.teacherassistant.ui.main.signInFragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.teacherassistant.R
import com.example.teacherassistant.common.Constants
import com.example.teacherassistant.common.OpenNextFragmentListener
import com.example.teacherassistant.databinding.SignInFragmentBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.messaging.FirebaseMessaging
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
        viewModel.checkRole(this, Constants.COLLECTION_FIRST_PATH)
        requireActivity().onBackPressedDispatcher.addCallback(requireActivity(),
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    requireActivity().finish()
                }
            })
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
                viewModel.checkRole(this, Constants.COLLECTION_FIRST_PATH)
                val userInfo = viewModel.getMapUserInfo()
                FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
                    userInfo[Constants.TOKEN] = token
                    if (binding.autoCompleteTextView.text.toString() == Constants.TEACHER ||
                        binding.autoCompleteTextView.text.toString() == Constants.STUDENT
                    ) {
                        if (binding.autoCompleteTextView.text.toString() == Constants.TEACHER) userInfo[Constants.STATUS] =
                            Constants.POSITIVE_STAT
                        if (binding.autoCompleteTextView.text.toString() == Constants.STUDENT) userInfo[Constants.STATUS] =
                            Constants.NEGATIVE_STAT
                    }
                    viewModel.setUserInfo(userInfo, Constants.COLLECTION_FIRST_PATH)
                }
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.signInFailed),
                    Toast.LENGTH_SHORT
                )
            }
        }
    }

    override fun openNextFragment(path: String) {
        if (viewModel.checkState()) {
            val bundle = Bundle()
            bundle.putString(Constants.ROLE, path)
            findNavController().navigate(R.id.mainFragment, bundle)
        }
    }
}