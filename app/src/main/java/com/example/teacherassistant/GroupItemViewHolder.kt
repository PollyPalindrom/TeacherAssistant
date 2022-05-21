package com.example.teacherassistant

import androidx.recyclerview.widget.RecyclerView
import com.example.teacherassistant.common.Group
import com.example.teacherassistant.databinding.GroupItemBinding

class GroupItemViewHolder(private val binding: GroupItemBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(group: Group) {
        binding.name.text = group.name
        binding.title.text = group.title
        binding.root.setOnClickListener {

        }
    }
}