package com.example.teacherassistant.ui.main.recycler

import androidx.recyclerview.widget.DiffUtil
import com.example.teacherassistant.common.Note

class NoteDiffCallback : DiffUtil.ItemCallback<Note>() {
    override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
        return oldItem.title == newItem.title && oldItem.message == newItem.message
    }
}