package com.example.filemanager.utils

import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

fun AppCompatActivity.showToast(text: String) =
    Toast.makeText(this, text, Toast.LENGTH_LONG).show()

fun Fragment.showToast(text: String) =
    Toast.makeText(this.context, text, Toast.LENGTH_LONG).show()