package com.example.teacherassistant.ui.main.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.example.teacherassistant.common.Note
import com.example.teacherassistant.databinding.GroupNoteItemBinding

class NoteAdapter : ListAdapter<Note, NoteItemViewHolder>(NoteDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteItemViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = GroupNoteItemBinding.inflate(layoutInflater, parent, false)
        return NoteItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NoteItemViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }
}