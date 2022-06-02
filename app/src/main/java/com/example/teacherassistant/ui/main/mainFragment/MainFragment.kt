package com.example.teacherassistant.ui.main.mainFragment

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenStarted
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.teacherassistant.R
import com.example.teacherassistant.common.Constants
import com.example.teacherassistant.common.OpenEmailWindowListener
import com.example.teacherassistant.common.OpenNextFragmentListener
import com.example.teacherassistant.databinding.EmailWindowBinding
import com.example.teacherassistant.databinding.GroupCreationWindowBinding
import com.example.teacherassistant.databinding.MainFragmentBinding
import com.example.teacherassistant.ui.main.firebaseService.FirebaseService
import com.example.teacherassistant.ui.main.recycler.GroupAdapter
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainFragment : Fragment(), OpenNextFragmentListener, OpenEmailWindowListener {

    private val viewModel by viewModels<MainViewModel>()
    private lateinit var binding: MainFragmentBinding
    private lateinit var groupBinding: GroupCreationWindowBinding
    private lateinit var emailBinding: EmailWindowBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MainFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val groupAdapter =
            arguments?.getString(Constants.ROLE)?.let { GroupAdapter(this, this, it) }
        FirebaseService.sharedPref =
            requireActivity().getSharedPreferences(
                Constants.SHARED_PREF_NAME,
                Context.MODE_PRIVATE
            )
        FirebaseMessaging.getInstance().token.addOnSuccessListener {
            FirebaseService.token = it
            viewModel.setNewToken(
                it,
                Constants.COLLECTION_FIRST_PATH,
                Constants.COLLECTION_SECOND_PATH,
                Constants.COLLECTION_THIRD_PATH_STUDENTS
            )
        }
        viewModel.subscribeGroupListChanges(
            Constants.COLLECTION_FIRST_PATH,
            Constants.COLLECTION_SECOND_PATH
        )

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.whenStarted {
                viewModel.groupsListOpen.collect {
                    if (it?.groups != null) {
                        groupAdapter?.submitList(it.groups)
                        binding.GroupRecycler.scrollToPosition(0)
                    }
                }
            }
        }
        if (arguments?.getString(Constants.ROLE) == Constants.STUDENT) {
            binding.addGroupButton.apply {
                visibility = View.INVISIBLE
                isClickable = false
            }
        }
        binding.addGroupButton.setOnClickListener {
            openGroupWindow()
        }
        binding.GroupRecycler.apply {
            adapter = groupAdapter
            layoutManager = LinearLayoutManager(context)
        }
        requireActivity().onBackPressedDispatcher.addCallback(requireActivity(),
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    requireActivity().finish()
                }
            })
    }

    private fun openGroupWindow() {
        val codeDialog = AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.Group))
            .setMessage(getString(R.string.groupWindowMessage))
        groupBinding = GroupCreationWindowBinding.inflate(LayoutInflater.from(requireContext()))
        codeDialog.setView(groupBinding.root)
        codeDialog.setPositiveButton(getString(R.string.positiveButton))
        { _, _ ->
            if (groupBinding.nameField.text.toString()
                    .isNotBlank() && groupBinding.titleField.text.toString().isNotBlank()
            ) {
                viewModel.createGroup(
                    Constants.COLLECTION_FIRST_PATH,
                    Constants.COLLECTION_SECOND_PATH,
                    groupBinding.nameField.text.toString(),
                    groupBinding.titleField.text.toString()
                )
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.textErrorMessage),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        codeDialog.show()
    }

    override fun openNextFragment(path: String) {
        if (viewModel.checkState()) {
            val bundle = Bundle()
            bundle.putString(Constants.GROUP_ID, path)
            bundle.putString(
                Constants.ROLE,
                arguments?.getString(Constants.ROLE)
            )
            findNavController().navigate(R.id.notesFragment, bundle)
        }
    }

    override fun openEmailWindow(id: String, title: String, name: String) {
        val codeDialog = AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.emailWindowTitle))
        emailBinding = EmailWindowBinding.inflate(LayoutInflater.from(requireContext()))
        codeDialog.setView(emailBinding.root)
        codeDialog.setPositiveButton(getString(R.string.positiveButton))
        { _, _ ->
            if (emailBinding.emailField.text.toString().isNotBlank()) {
                viewModel.addStudent(
                    emailBinding.emailField.text.toString(),
                    Constants.COLLECTION_FIRST_PATH,
                    Constants.COLLECTION_SECOND_PATH,
                    id,
                    Constants.COLLECTION_THIRD_PATH_STUDENTS,
                    title, name
                )
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.textErrorMessage),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        codeDialog.show()
    }
}