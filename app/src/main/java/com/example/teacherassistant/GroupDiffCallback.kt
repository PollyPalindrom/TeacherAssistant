package com.example.teacherassistant

import androidx.recyclerview.widget.DiffUtil
import com.example.teacherassistant.common.Group

class GroupDiffCallback : DiffUtil.ItemCallback<Group>() {
    override fun areItemsTheSame(oldItem: Group, newItem: Group): Boolean {
        return oldItem.name == newItem.name && oldItem.title == newItem.title && oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Group, newItem: Group): Boolean {
        return oldItem.name == newItem.name && oldItem.title == newItem.title
    }
}