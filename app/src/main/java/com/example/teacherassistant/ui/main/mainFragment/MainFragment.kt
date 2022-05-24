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
        val groupAdapter = arguments?.getString("Role")?.let { GroupAdapter(this, this, it) }
        FirebaseService.sharedPref =
            requireActivity().getSharedPreferences(
                getString(R.string.sharedPref),
                Context.MODE_PRIVATE
            )
        FirebaseMessaging.getInstance().token.addOnSuccessListener {
            FirebaseService.token = it
            viewModel.setNewToken(
                it,
                getString(R.string.collectionFirstPath),
                getString(R.string.collectionSecondPath),
                getString(R.string.collectionThirdPathStudents)
            )
        }
        viewModel.subscribeGroupListChanges(
            getString(R.string.collectionFirstPath),
            getString(R.string.collectionSecondPath)
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
        if (arguments?.getString(getString(R.string.role)) == getString(R.string.student)) {
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
            .setTitle(getString(R.string.collectionSecondPath))
            .setMessage(getString(R.string.groupWindowMessage))
        groupBinding = GroupCreationWindowBinding.inflate(LayoutInflater.from(requireContext()))
        codeDialog.setView(groupBinding.root)
        codeDialog.setPositiveButton(getString(R.string.positiveButton))
        { _, _ ->
            if (groupBinding.nameField.text.toString()
                    .isNotBlank() && groupBinding.titleField.text.toString().isNotBlank()
            ) {
                viewModel.createGroup(
                    getString(R.string.collectionFirstPath),
                    getString(R.string.collectionSecondPath),
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
            bundle.putString(getString(R.string.GroupId), path)
            bundle.putString(
                getString(R.string.role),
                arguments?.getString(getString(R.string.role))
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
                    getString(R.string.collectionFirstPath),
                    getString(R.string.collectionSecondPath),
                    id,
                    getString(R.string.collectionThirdPathStudents),
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