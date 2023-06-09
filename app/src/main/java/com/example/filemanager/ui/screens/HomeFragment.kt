package com.example.filemanager.ui.screens

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.domain.entity.FileGroup
import com.example.filemanager.app.FileManagerApp
import com.example.filemanager.databinding.FragmentHomeBinding
import com.example.filemanager.ui.recycler.FileListAdapter
import com.example.filemanager.ui.vm.HomeViewModel
import com.example.filemanager.ui.vm.ViewModelFactory
import com.example.filemanager.utils.Constants.HOME_FRAGMENT_BINDING_IS_NULL
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

    private var _binding: FragmentHomeBinding? = null
    private val binding: FragmentHomeBinding
        get() = _binding ?: throw RuntimeException(HOME_FRAGMENT_BINDING_IS_NULL)

    private lateinit var recentUpdatedListAdapter: FileListAdapter

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
        init()
        showRecentUpdatedFileList()
    }

    private fun showRecentUpdatedFileList() {
        viewModel.findLastModifiedFileList()
        viewModel.lastModifiedFileList.observe(viewLifecycleOwner) {
            recentUpdatedListAdapter.submitList(it)
        }
    }

    private fun init() {
        with(binding) {
            rcView.layoutManager = LinearLayoutManager(activity)
            recentUpdatedListAdapter = FileListAdapter(requireContext())
            rcView.adapter = recentUpdatedListAdapter
        }
    }

    private fun initButtons() {
        with(binding) {
            buttonImageFiles.setOnClickListener {
                findNavController().navigate(
                    HomeFragmentDirections.actionNavHomeToFilesByTypeFragment(
                        FileGroup.IMAGES
                    ).setFilesGroup(FileGroup.IMAGES)
                )
            }
            buttonVideoFiles.setOnClickListener {
                findNavController().navigate(
                    HomeFragmentDirections.actionNavHomeToFilesByTypeFragment(
                        FileGroup.VIDEOS
                    ).setFilesGroup(FileGroup.VIDEOS)
                )
            }
            buttonApkFiles.setOnClickListener {
                findNavController().navigate(
                    HomeFragmentDirections.actionNavHomeToFilesByTypeFragment(
                        FileGroup.APK
                    ).setFilesGroup(FileGroup.APK)
                )
            }
            buttonAudioFiles.setOnClickListener {
                findNavController().navigate(
                    HomeFragmentDirections.actionNavHomeToFilesByTypeFragment(
                        FileGroup.AUDIO
                    ).setFilesGroup(FileGroup.AUDIO)
                )
            }
            buttonOtherFiles.setOnClickListener {
                findNavController().navigate(
                    HomeFragmentDirections.actionNavHomeToFilesByTypeFragment(
                        FileGroup.DOCUMENTS
                    ).setFilesGroup(FileGroup.DOCUMENTS)
                )
            }
            buttonDownloadFiles.setOnClickListener {
                findNavController().navigate(
                    HomeFragmentDirections.actionNavHomeToFilesByTypeFragment(
                        FileGroup.DOCUMENTS
                    ).setFilesGroup(FileGroup.AUDIO)
                )
            }
            buttonDocFiles.setOnClickListener {
                findNavController().navigate(
                    HomeFragmentDirections.actionNavHomeToFilesByTypeFragment(
                        FileGroup.DOCUMENTS
                    ).setFilesGroup(FileGroup.DOCUMENTS)
                )
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}