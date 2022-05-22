package com.example.teacherassistant.ui.main

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenStarted
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.teacherassistant.R
import com.example.teacherassistant.common.OpenNextFragmentListener
import com.example.teacherassistant.databinding.GroupCreationWindowBinding
import com.example.teacherassistant.databinding.MainFragmentBinding
import com.example.teacherassistant.ui.main.recycler.GroupAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainFragment : Fragment(), OpenNextFragmentListener {

    private val viewModel by viewModels<MainViewModel>()
    private lateinit var binding: MainFragmentBinding
    private val groupAdapter = GroupAdapter(this)
    private lateinit var groupBinding: GroupCreationWindowBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MainFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getGroupList("User", "Group")
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.whenStarted {
                viewModel.groupsListOpen.collect {
                    if (it?.groups != null) {
                        groupAdapter.submitList(it.groups)
                        binding.GroupRecycler.scrollToPosition(0)
                    }
                }
            }
        }
        if (arguments?.getString("Role") != "Teacher") {
            binding.addGroupButton.apply {
                visibility = View.INVISIBLE
                isClickable = false
            }
        }
        binding.addGroupButton.setOnClickListener {
            openCodeWindow()
        }
        binding.GroupRecycler.apply {
            adapter = groupAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun openCodeWindow() {
        val codeDialog = AlertDialog.Builder(requireContext())
            .setTitle("Group")
            .setMessage("Enter name and tittle of group")
        groupBinding = GroupCreationWindowBinding.inflate(LayoutInflater.from(requireContext()))
        codeDialog.setView(groupBinding.root)
        codeDialog.setPositiveButton("Ok")
        { _, _ ->
            if (groupBinding.nameField.text.toString()
                    .isNotBlank() && groupBinding.titleField.text.toString().isNotBlank()
            ) {
                viewModel.createGroup(
                    "User",
                    "Group",
                    groupBinding.nameField.text.toString(),
                    groupBinding.titleField.text.toString()
                )
            } else {
                Toast.makeText(requireContext(), "wrong", Toast.LENGTH_LONG).show()
            }
        }
        codeDialog.show()
    }

    override fun openNextFragment(path: String) {
        if (viewModel.checkState()) {
            val bundle = Bundle()
            bundle.putString("GroupId", viewModel.getUserUid() + path)
            bundle.putString("Role", arguments?.getString("Role"))
            findNavController().navigate(R.id.notesFragment, bundle)
        }
    }
}