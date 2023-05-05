package com.example.domain.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
sealed class FileType: Parcelable {
    object FOLDER : FileType()
    object DOC : FileType()
    object PDF: FileType()
    object ZIP: FileType()
    object OTHER: FileType()
    object WAV: FileType()
    object JPEG: FileType()
    object JPG: FileType()
    object PNG: FileType()
    object MP3: FileType()
    object MP4: FileType()
    object APK: FileType()
}


