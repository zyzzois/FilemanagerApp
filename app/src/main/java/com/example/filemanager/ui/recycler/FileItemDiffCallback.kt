package com.example.filemanager.ui.recycler

import androidx.recyclerview.widget.DiffUtil
import com.example.domain.entity.FileEntity

object FileItemDiffCallback : DiffUtil.ItemCallback<FileEntity>() {
    override fun areItemsTheSame(oldItem: FileEntity, newItem: FileEntity): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: FileEntity, newItem: FileEntity): Boolean {
        return oldItem == newItem
    }
}