package com.example.filemanager.ui.screens

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Process
import android.provider.Settings
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.filemanager.databinding.ActivityStartBinding
import com.example.filemanager.utils.showToast
import java.io.File

class StartActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityStartBinding.inflate(layoutInflater)
    }

    private var launcher: ActivityResultLauncher<Intent>? = null

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        if (hasAllFilesAccessPermission()) {
            startActivity(MainActivity.newIntentOpenMainActivity(this@StartActivity))
            finish()
        }

        binding.buttonAccept.setOnClickListener {
            requestAllFilesAccessPermission()
        }

        launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (hasAllFilesAccessPermission()) {
                showToast(ACCESS_ALLOWED)
                startActivity(MainActivity.newIntentOpenMainActivity(this@StartActivity))
                finish()
            }
            else {
                showToast(ACCESS_DENIED)
            }

        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun requestAllFilesAccessPermission() {
        val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
        intent.addCategory(CATEGORY)
        intent.data = Uri.parse(String.format(PACKAGE, applicationContext.packageName))
        launcher?.launch(intent)
    }

    private fun hasAllFilesAccessPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            applicationContext.checkUriPermission(
                Uri.fromFile(File("/")),
                Process.myPid(),
                Process.myUid(),
                Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    companion object {
        private const val CATEGORY = "android.intent.category.DEFAULT"
        private const val PACKAGE = "package:%s"
        private const val ACCESS_DENIED = "Вы не предоставили разрешение"
        private const val ACCESS_ALLOWED = "Вы успешно предоставили разрешение"
    }
}