package com.example.filemanager.ui.screens

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
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

    private val sharedPreferences by lazy {
        getSharedPreferences(TIME_TABLE, Context.MODE_PRIVATE)
    }
    private val sharedPreferencesEditor by lazy { sharedPreferences.edit() }

    override fun onCreate(savedInstanceState: Bundle?) {
        component.inject(this)
        super.onCreate(savedInstanceState)
        parseLastStartTime()
        setContentView(binding.root)
        setSupportActionBar(binding.appBarMain.toolbar)
        val drawerLayout = binding.drawerLayout
        val navView = binding.navView

        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.nav_home, R.id.nav_folders), drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    private fun parseLastStartTime() {
        val currentTime = System.currentTimeMillis()
        val lastRunTime = sharedPreferences.getLong(LAST_RUN_TIME, currentTime)
        viewModel.setLastRunTime(lastRunTime)
        viewModel.uploadRecentUpdatedFilesToDatabase()
        sharedPreferencesEditor.putLong(LAST_RUN_TIME, System.currentTimeMillis())
        sharedPreferencesEditor.apply()
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

    companion object {
        const val REQUEST_CODE = 200
        private const val TIME_TABLE = "TIME_TABLE"
        private const val LAST_RUN_TIME = "LAST_RUN_TIME"
        fun newIntentOpenMainActivity(context: Context) = Intent(context, MainActivity::class.java)
    }
}