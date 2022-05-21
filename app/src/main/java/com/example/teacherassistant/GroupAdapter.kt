package com.example.teacherassistant

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.example.teacherassistant.common.Group
import com.example.teacherassistant.databinding.GroupItemBinding

class GroupAdapter : ListAdapter<Group, GroupItemViewHolder>(GroupDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupItemViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = GroupItemBinding.inflate(layoutInflater, parent, false)
        return GroupItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GroupItemViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }
}