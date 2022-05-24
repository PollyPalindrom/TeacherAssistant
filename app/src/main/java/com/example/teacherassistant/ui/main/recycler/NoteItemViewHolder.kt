package com.example.teacherassistant.ui.main.recycler

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.teacherassistant.common.Note
import com.example.teacherassistant.databinding.GroupNoteItemBinding

class NoteItemViewHolder(
    private val binding: GroupNoteItemBinding
) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(note: Note) {
        binding.addStudent.apply {
            isClickable = false
            visibility = View.INVISIBLE
        }
        binding.name.text = note.title
        binding.title.text = note.message
    }
}