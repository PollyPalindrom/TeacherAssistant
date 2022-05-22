package com.example.teacherassistant.ui.main.recycler

import androidx.recyclerview.widget.RecyclerView
import com.example.teacherassistant.common.Note
import com.example.teacherassistant.databinding.GroupNoteItemBinding

class NoteItemViewHolder(
    private val binding: GroupNoteItemBinding
) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(note: Note) {
        binding.name.text = note.title
        binding.title.text = note.message
    }
}