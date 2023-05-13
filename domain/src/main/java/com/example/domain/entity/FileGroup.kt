package com.example.domain.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class FileGroup: Parcelable {
    IMAGES, VIDEOS, DOCUMENTS, AUDIO, APK, ARCHIVES, DOWNLOADS
}
