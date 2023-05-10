package com.example.filemanager.ui.screens

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import com.example.domain.entity.FileEntity
import com.example.filemanager.R
import com.example.filemanager.databinding.ActivityMainBinding
import com.example.filemanager.databinding.ActivityVkidactivityBinding
import com.example.filemanager.di.ViewModelKey
import com.example.filemanager.utils.Constants.ERROR
import com.example.filemanager.vkid.VKServerUploadInfo2
import com.example.filemanager.vkid.VKWallPostCommand
import com.vk.api.sdk.VK
import com.vk.api.sdk.VKApiCallback
import com.vk.api.sdk.auth.VKAuthenticationResult
import com.vk.api.sdk.auth.VKScope
import java.io.File

class VKIDActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityVkidactivityBinding.inflate(layoutInflater)
    }

    private val authLauncher = VK.login(this as ComponentActivity) { result : VKAuthenticationResult ->
        when (result) {
            is VKAuthenticationResult.Success -> {
                binding.tvState.visibility = View.VISIBLE
                binding.buttonBack.visibility = View.VISIBLE
            }
            is VKAuthenticationResult.Failed -> {
                binding.tvState.text = ERROR
                binding.buttonBack.visibility = View.VISIBLE
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        authLauncher.launch(arrayListOf(VKScope.DOCS))
        binding.buttonBack.setOnClickListener {
            finish()
        }
    }

    //TODO()
    private fun shareFile(file: FileEntity) {
        if (VK.isLoggedIn()) {
            val selectedFile = file.file
            val uriFile = Uri.fromFile(selectedFile)
            val fileName = file.filename

            VK.execute(
                VKWallPostCommand(
                    "trying to post",
                    uriFile,
                    fileName,
                    0
                ), object:
                    VKApiCallback<VKServerUploadInfo2> {
                    override fun fail(error: Exception) {
                        println(error.toString())
                    }
                    override fun success(result: VKServerUploadInfo2) {
                    }
                })

        }
    }

}