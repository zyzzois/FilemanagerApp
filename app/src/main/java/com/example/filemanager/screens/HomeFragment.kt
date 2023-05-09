package com.example.filemanager.screens

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.domain.entity.FileGroup
import com.example.domain.entity.FileType
import com.example.filemanager.app.FileManagerApp
import com.example.filemanager.recycler.FileListAdapter
import com.example.filemanager.databinding.FragmentHomeBinding
import com.example.filemanager.vm.FoldersViewModel
import com.example.filemanager.vm.HomeViewModel
import com.example.filemanager.vm.ViewModelFactory
import javax.inject.Inject

class HomeFragment : Fragment() {

    private val component by lazy {
        (requireActivity().application as FileManagerApp).component
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel by lazy {
        ViewModelProvider(requireActivity(), viewModelFactory)[HomeViewModel::class.java]
    }

    private lateinit var homeFragmentAdapter: FileListAdapter

    private var _binding: FragmentHomeBinding? = null
    private val binding: FragmentHomeBinding
        get() = _binding ?: throw RuntimeException(BINDING_EXCEPTION_MESSAGE)


    override fun onAttach(context: Context) {
        component.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initButtons()
    }

    private fun initButtons() {
        with(binding) {
            buttonImageFiles.setOnClickListener {
                findNavController().navigate(
                    HomeFragmentDirections.actionNavHomeToFilesByTypeFragment(FileGroup.IMAGES).setFilesGroup(FileGroup.IMAGES)
                )
            }
            buttonVideoFiles.setOnClickListener {
                findNavController().navigate(
                    HomeFragmentDirections.actionNavHomeToFilesByTypeFragment(FileGroup.VIDEOS).setFilesGroup(FileGroup.VIDEOS)
                )
            }
            buttonApkFiles.setOnClickListener {
                findNavController().navigate(
                    HomeFragmentDirections.actionNavHomeToFilesByTypeFragment(FileGroup.APK).setFilesGroup(FileGroup.APK)
                )
            }
            buttonAudioFiles.setOnClickListener {
                findNavController().navigate(
                    HomeFragmentDirections.actionNavHomeToFilesByTypeFragment(FileGroup.AUDIO).setFilesGroup(FileGroup.AUDIO)
                )
            }
            buttonOtherFiles.setOnClickListener {
                findNavController().navigate(
                    HomeFragmentDirections.actionNavHomeToFilesByTypeFragment(FileGroup.DOCUMENTS).setFilesGroup(FileGroup.DOCUMENTS)
                )
            }
            buttonDownloadFiles.setOnClickListener {
                findNavController().navigate(
                    HomeFragmentDirections.actionNavHomeToFilesByTypeFragment(FileGroup.DOCUMENTS).setFilesGroup(FileGroup.AUDIO)
                )
            }
            buttonDocFiles.setOnClickListener {
                findNavController().navigate(
                    HomeFragmentDirections.actionNavHomeToFilesByTypeFragment(FileGroup.DOCUMENTS).setFilesGroup(FileGroup.DOCUMENTS)
                )
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val BINDING_EXCEPTION_MESSAGE = "HomeFragmentBinding = null"
    }
}