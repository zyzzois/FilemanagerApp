package com.example.filemanager.ui.screens

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import com.example.filemanager.R
import com.example.filemanager.databinding.ActivityMainBinding
import com.example.filemanager.databinding.ActivityVkidactivityBinding
import com.example.filemanager.di.ViewModelKey
import com.example.filemanager.utils.Constants.ERROR
import com.vk.api.sdk.VK
import com.vk.api.sdk.auth.VKAuthenticationResult
import com.vk.api.sdk.auth.VKScope

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

}