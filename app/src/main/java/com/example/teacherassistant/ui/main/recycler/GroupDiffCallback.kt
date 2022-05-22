package com.example.teacherassistant.ui.main.recycler

import androidx.recyclerview.widget.DiffUtil
import com.example.teacherassistant.common.Group

class GroupDiffCallback : DiffUtil.ItemCallback<Group>() {
    override fun areItemsTheSame(oldItem: Group, newItem: Group): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Group, newItem: Group): Boolean {
        return oldItem.name == newItem.name && oldItem.title == newItem.title
    }
}