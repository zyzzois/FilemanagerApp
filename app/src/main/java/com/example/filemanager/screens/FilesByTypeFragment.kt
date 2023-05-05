package com.example.filemanager.screens

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.domain.entity.FileType
import com.example.filemanager.R
import com.example.filemanager.app.FileManagerApp
import com.example.filemanager.databinding.FragmentFilesByTypeBinding
import com.example.filemanager.databinding.FragmentFoldersBinding
import com.example.filemanager.recycler.FileListAdapter
import com.example.filemanager.vm.FilesByTypeViewModel
import com.example.filemanager.vm.FoldersViewModel
import com.example.filemanager.vm.ViewModelFactory
import java.lang.RuntimeException
import javax.inject.Inject

class FilesByTypeFragment : Fragment() {
    private val args by navArgs<FilesByTypeFragmentArgs>()

    private val component by lazy {
        (requireActivity().application as FileManagerApp).component
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private var _binding: FragmentFilesByTypeBinding? = null
    private val binding: FragmentFilesByTypeBinding
        get() = _binding ?: throw RuntimeException(BINDING_EXCEPTION_MESSAGE)

    private lateinit var filesByTypeAdapter: FileListAdapter

    private val viewModel by lazy {
        ViewModelProvider(requireActivity(), viewModelFactory)[FilesByTypeViewModel::class.java]
    }

    override fun onAttach(context: Context) {
        component.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFilesByTypeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        showFileList()
    }



    private fun initRecyclerView() {
        with(binding) {
            rcView.layoutManager = LinearLayoutManager(activity)
            filesByTypeAdapter = FileListAdapter(requireContext())
            rcView.adapter = filesByTypeAdapter
        }
    }

    private fun showFileList() {
        viewModel.updateList(args.filesGroup)
        viewModel.fileList.observe(viewLifecycleOwner) {
            filesByTypeAdapter.submitList(it)
        }
    }


    companion object {
        private const val BINDING_EXCEPTION_MESSAGE = "FragmentFilesByTypeBinding = null"
    }
}