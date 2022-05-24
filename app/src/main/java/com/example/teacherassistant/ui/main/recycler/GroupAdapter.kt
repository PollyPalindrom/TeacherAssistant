package com.example.teacherassistant.ui.main.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.example.teacherassistant.common.Group
import com.example.teacherassistant.common.OpenEmailWindowListener
import com.example.teacherassistant.common.OpenNextFragmentListener
import com.example.teacherassistant.databinding.GroupNoteItemBinding

class GroupAdapter(
    private val listener: OpenNextFragmentListener,
    private val dialogListener: OpenEmailWindowListener,
    private val role: String
) :
    ListAdapter<Group, GroupItemViewHolder>(
        GroupDiffCallback()
    ) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupItemViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = GroupNoteItemBinding.inflate(layoutInflater, parent, false)
        return GroupItemViewHolder(binding, listener, dialogListener)
    }

    override fun onBindViewHolder(holder: GroupItemViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it, role) }
    }
}