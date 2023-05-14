package com.example.filemanager.ui.screens

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.filemanager.R
import com.example.filemanager.app.FileManagerApp
import com.example.filemanager.databinding.ActivityMainBinding
import com.example.filemanager.ui.vm.StartViewModel
import com.example.filemanager.ui.vm.ViewModelFactory
import com.example.filemanager.utils.Constants.SEARCH
import com.example.filemanager.utils.showToast
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    private val component by lazy {
        (application as FileManagerApp).component
    }

    private val navController by lazy {
        findNavController(R.id.nav_host_fragment_content_main)
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[StartViewModel::class.java]
    }

    private var permissionGranted = false
    private lateinit var appBarConfiguration: AppBarConfiguration

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        component.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.appBarMain.toolbar)
        uploadFilesHashesToDatabase()
        val drawerLayout = binding.drawerLayout
        val navView = binding.navView

        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.nav_home, R.id.nav_folders), drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    private fun uploadFilesHashesToDatabase() {
        viewModel.clearDatabase()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE)
            permissionGranted = grantResults[0] == PackageManager.PERMISSION_GRANTED
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onDestroy() {
        Log.d("ZOIS", "onDestroy")
        super.onDestroy()
        viewModel.uploadFilesHashesToDatabase()
    }

    companion object {
        const val REQUEST_CODE = 200
        fun newIntentOpenMainActivity(context: Context) = Intent(context, MainActivity::class.java)
    }
}