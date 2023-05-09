package com.example.filemanager.ui.recycler

import android.content.Context
import android.text.format.Formatter
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.example.domain.entity.FileEntity
import com.example.domain.entity.FileType
import com.example.filemanager.R
import com.example.filemanager.databinding.FileListItemBinding


class FileListAdapter(private val context: Context): ListAdapter<FileEntity, FileItemViewHolder>(
    FileItemDiffCallback
) {

    var onFileItemLongClickListener: ((FileEntity) -> Unit)? = null
    var onFileItemClickListener: ((FileEntity) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileItemViewHolder {
        val binding = FileListItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return FileItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FileItemViewHolder, position: Int) {
        val fileItem = getItem(position)
        val binding = holder.binding

        binding.root.setOnLongClickListener {
            onFileItemLongClickListener?.invoke(fileItem)
            true
        }
        binding.root.setOnClickListener {
            onFileItemClickListener?.invoke(fileItem)
        }

        with(binding) {

            when (fileItem.fileType) {
                is FileType.FOLDER -> {
                    imageFileIcon.setImageResource(R.drawable.ic_folder)

                }
                is FileType.PDF -> {
                    tvTimesTamp.text = Formatter.formatShortFileSize(context, fileItem.file.length())
                    imageFileIcon.setImageResource(R.drawable.ic_pdf)
                }
                is FileType.MP4 -> {
                    tvTimesTamp.text = Formatter.formatShortFileSize(context, fileItem.file.length())
                    imageFileIcon.setImageResource(R.drawable.ic_mp4)
                }
                is FileType.MP3 -> {
                    tvTimesTamp.text = Formatter.formatShortFileSize(context, fileItem.file.length())
                    imageFileIcon.setImageResource(R.drawable.ic_mp3)
                }
                is FileType.JPEG -> {
                    tvTimesTamp.text = Formatter.formatShortFileSize(context, fileItem.file.length())
                    imageFileIcon.setImageResource(R.drawable.ic_image)
                }
                is FileType.PNG -> {
                    tvTimesTamp.text = Formatter.formatShortFileSize(context, fileItem.file.length())
                    imageFileIcon.setImageResource(R.drawable.ic_image)
                }
                is FileType.JPG -> {
                    tvTimesTamp.text = Formatter.formatShortFileSize(context, fileItem.file.length())
                    imageFileIcon.setImageResource(R.drawable.ic_image)
                }
                is FileType.ZIP -> {
                    tvTimesTamp.text = Formatter.formatShortFileSize(context, fileItem.file.length())
                    imageFileIcon.setImageResource(R.drawable.ic_zip)
                }
                else -> {

                }
            }
            tvFileName.text = fileItem.filename
        }
    }

}