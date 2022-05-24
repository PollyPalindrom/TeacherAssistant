package com.example.teacherassistant.ui.main.notesFragment

import android.app.AlertDialog
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
        arguments?.getString(getString(R.string.GroupId))?.let {
            viewModel.subscribeNoteListChanges(
                getString(R.string.collectionFirstPath),
                getString(R.string.collectionSecondPath),
                it,
                getString(R.string.collectionThirdPath)
            )
        }

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

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.whenStarted {
                viewModel.notificationStateOpen.collect {
                    if (it?.error == null) {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.success),
                            Toast.LENGTH_SHORT
                        )
                    } else {
                        Toast.makeText(requireContext(), it.error, Toast.LENGTH_SHORT)
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
            openNoteWindow()
        }
        binding.noteRecycler.apply {
            adapter = notesAdapter
            layoutManager = LinearLayoutManager(context)
        }

        requireActivity().onBackPressedDispatcher.addCallback(requireActivity(),
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().popBackStack()
                }
            })
    }

    private fun openNoteWindow() {
        val codeDialog = AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.note))
            .setMessage(getString(R.string.noteWindowMessage))
        noteBinding = NoteCreateWindowBinding.inflate(LayoutInflater.from(requireContext()))
        codeDialog.setView(noteBinding.root)
        codeDialog.setPositiveButton(getString(R.string.positiveButton))
        { _, _ ->
            if (noteBinding.titleField.text.toString()
                    .isNotBlank() && noteBinding.textField.text.toString().isNotBlank()
            ) {
                arguments?.getString(getString(R.string.GroupId))?.let {
                    viewModel.createNote(
                        getString(R.string.collectionFirstPath),
                        getString(R.string.collectionSecondPath),
                        getString(R.string.collectionThirdPath),
                        it,
                        noteBinding.titleField.text.toString(),
                        noteBinding.textField.text.toString()
                    )
                }
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