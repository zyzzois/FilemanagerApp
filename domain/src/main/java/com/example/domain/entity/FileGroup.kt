package com.example.domain.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
sealed class FileGroup: Parcelable {
    object IMAGES: FileGroup()
    object VIDEOS: FileGroup()
    object DOCUMENTS: FileGroup()
    object AUDIO: FileGroup()
    object APK: FileGroup()
    object ARCHIVES: FileGroup()
    object DOWNLOADS: FileGroup()
    object MORE: FileGroup()
}
