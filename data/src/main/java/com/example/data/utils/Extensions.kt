package com.example.data.utils

import com.example.data.repository.FileManagerRepositoryImpl
import com.example.domain.entity.FileType
import java.io.File

fun File.extensionIs(extension: String): Boolean {
    return this.name.lowercase().endsWith(extension)
}

fun File.extensionType(): FileType {
    if (this.extensionIs(FileManagerRepositoryImpl.extension[0]))
        return FileType.JPEG
    else if (this.extensionIs(FileManagerRepositoryImpl.extension[1]))
        return FileType.JPG
    else if (this.extensionIs(FileManagerRepositoryImpl.extension[2]))
        return FileType.PNG
    else if (this.extensionIs(FileManagerRepositoryImpl.extension[3]))
        return FileType.MP3
    else if (this.extensionIs(FileManagerRepositoryImpl.extension[4]))
        return FileType.WAV
    else if (this.extensionIs(FileManagerRepositoryImpl.extension[5]))
        return FileType.MP4
    else if (this.extensionIs(FileManagerRepositoryImpl.extension[6]))
        return FileType.PDF
    else if (this.extensionIs(FileManagerRepositoryImpl.extension[7]))
        return FileType.DOC
    else if (this.extensionIs(FileManagerRepositoryImpl.extension[8]))
        return FileType.APK
    else if (this.extensionIs(FileManagerRepositoryImpl.extension[9]))
        return FileType.ZIP

    return FileType.FOLDER
}

