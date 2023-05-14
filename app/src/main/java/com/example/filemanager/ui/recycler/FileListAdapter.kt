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
                    tvSize.text = "${fileItem.numOfFilesInFolder} Files"
                    tvTimesTampString.text = fileItem.timestampString
                }
                is FileType.PDF -> {
                    tvSize.text = Formatter.formatShortFileSize(context, fileItem.file.length())
                    imageFileIcon.setImageResource(R.drawable.ic_pdf)
                    tvTimesTampString.text = fileItem.timestampString
                }
                is FileType.DOC -> {
                    tvSize.text = Formatter.formatShortFileSize(context, fileItem.file.length())
                    imageFileIcon.setImageResource(R.drawable.ic_pdf)
                    tvTimesTampString.text = fileItem.timestampString
                }
                is FileType.MP4 -> {
                    tvSize.text = Formatter.formatShortFileSize(context, fileItem.file.length())
                    imageFileIcon.setImageResource(R.drawable.ic_mp4)
                    tvTimesTampString.text = fileItem.timestampString
                }
                is FileType.MP3 -> {
                    tvSize.text = Formatter.formatShortFileSize(context, fileItem.file.length())
                    imageFileIcon.setImageResource(R.drawable.ic_mp3)
                    tvTimesTampString.text = fileItem.timestampString
                }
                is FileType.JPEG -> {
                    tvSize.text = Formatter.formatShortFileSize(context, fileItem.file.length())
                    imageFileIcon.setImageResource(R.drawable.ic_image)
                    tvTimesTampString.text = fileItem.timestampString
                }
                is FileType.PNG -> {
                    tvSize.text = Formatter.formatShortFileSize(context, fileItem.file.length())
                    imageFileIcon.setImageResource(R.drawable.ic_image)
                    tvTimesTampString.text = fileItem.timestampString
                }
                is FileType.JPG -> {
                    tvSize.text = Formatter.formatShortFileSize(context, fileItem.file.length())
                    imageFileIcon.setImageResource(R.drawable.ic_image)
                    tvTimesTampString.text = fileItem.timestampString
                }
                is FileType.ZIP -> {
                    tvSize.text = Formatter.formatShortFileSize(context, fileItem.file.length())
                    imageFileIcon.setImageResource(R.drawable.ic_zip)
                    tvTimesTampString.text = fileItem.timestampString
                }
                is FileType.APK -> {
                    tvSize.text = Formatter.formatShortFileSize(context, fileItem.file.length())
                    imageFileIcon.setImageResource(R.drawable.ic_apk_file)
                    tvTimesTampString.text = fileItem.timestampString
                }
                else -> imageFileIcon.setImageResource(R.drawable.ic_folder)
            }
            tvFileName.text = fileItem.filename
        }
    }
}