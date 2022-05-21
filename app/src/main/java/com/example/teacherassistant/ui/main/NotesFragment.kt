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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.teacherassistant.databinding.GroupCreationWindowBinding
import com.example.teacherassistant.databinding.NotesFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NotesFragment : Fragment() {

    private lateinit var binding: NotesFragmentBinding
    private val viewModel: NotesViewModel by viewModels<NotesViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = NotesFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getNoteList("User", "Group",,"Notes")
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.whenStarted {
                viewModel.noteListOpen.collect {
                    if (it?.notes != null) {
                        notesAdapter.submitList(it.notes)
                        binding.NotesRecycler.scrollToPosition(0)
                    }
                }
            }
        }
        binding.addGroupButton.setOnClickListener {
            openNoteWindow()
        }
        binding.NotesRecycler.apply {
            adapter = notesAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun openNoteWindow() {
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

}