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
import com.example.teacherassistant.databinding.NoteCreateWindowBinding
import com.example.teacherassistant.databinding.NotesFragmentBinding
import com.example.teacherassistant.ui.main.recycler.NoteAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NotesFragment : Fragment() {

    private lateinit var binding: NotesFragmentBinding
    private val viewModel: NotesViewModel by viewModels<NotesViewModel>()
    private lateinit var noteBinding: NoteCreateWindowBinding
    private val notesAdapter = NoteAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = NotesFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.getString("GroupId")?.let { viewModel.getNoteList("User", "Group", it, "Notes") }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.whenStarted {
                viewModel.noteListOpen.collect {
                    if (it?.notes != null) {
                        notesAdapter.submitList(it.notes)
                        binding.noteRecycler.scrollToPosition(0)
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
            openNoteWindow()
        }
        binding.noteRecycler.apply {
            adapter = notesAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun openNoteWindow() {
        val codeDialog = AlertDialog.Builder(requireContext())
            .setTitle("Group")
            .setMessage("Enter name and tittle of group")
        noteBinding = NoteCreateWindowBinding.inflate(LayoutInflater.from(requireContext()))
        codeDialog.setView(noteBinding.root)
        codeDialog.setPositiveButton("Ok")
        { _, _ ->
            if (noteBinding.titleField.text.toString()
                    .isNotBlank() && noteBinding.textField.text.toString().isNotBlank()
            ) {
                arguments?.getString("GroupId")?.let {
                    viewModel.createNote(
                        "User",
                        "Group",
                        "Notes",
                        it,
                        noteBinding.titleField.text.toString(),
                        noteBinding.textField.text.toString()
                    )
                }
            } else {
                Toast.makeText(requireContext(), "wrong", Toast.LENGTH_LONG).show()
            }
        }
        codeDialog.show()
    }

}