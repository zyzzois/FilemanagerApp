package com.example.filemanager.ui.screens

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.filemanager.app.FileManagerApp
import com.example.filemanager.databinding.FragmentVKIDBinding
import com.example.filemanager.utils.Constants.FRAGMENT_VKID_BINDING_IS_NULL

class VKIDFragment : Fragment() {

    private val component by lazy {
        (requireActivity().application as FileManagerApp).component
    }

    private var _binding: FragmentVKIDBinding? = null
    private val binding: FragmentVKIDBinding
        get() = _binding ?: throw RuntimeException(FRAGMENT_VKID_BINDING_IS_NULL)

    override fun onAttach(context: Context) {
        component.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentVKIDBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonSignInVK.setOnClickListener {
            val intent = Intent(activity, StartActivity::class.java)
            startActivity(intent)
        }
        binding.buttonSkipAuth.setOnClickListener {

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}