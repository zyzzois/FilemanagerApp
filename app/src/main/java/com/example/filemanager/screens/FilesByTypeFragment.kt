package com.example.filemanager.screens

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.filemanager.app.FileManagerApp
import com.example.filemanager.databinding.FragmentFilesByTypeBinding
import com.example.filemanager.recycler.FileListAdapter
import com.example.filemanager.utils.FileOpener
import com.example.filemanager.vm.FilesByTypeViewModel
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
        setupClickListener()
    }

    private fun setupClickListener() {
        filesByTypeAdapter.onFileItemClickListener = {
            if (it.file.isDirectory) {
                findNavController().navigate(
                    FilesByTypeFragmentDirections.actionFilesByTypeFragmentSelf(
                        args.filesGroup
                    ).setFilesGroup(args.filesGroup).setPath(it.file.path)
                )
                viewModel.setPath(it.file.path)
            }
            else FileOpener().openFile(requireContext(), it.file)
        }
    }

    private fun initRecyclerView() {
        with(binding) {
            rcView.layoutManager = LinearLayoutManager(activity)
            filesByTypeAdapter = FileListAdapter(requireContext())
            rcView.adapter = filesByTypeAdapter
        }
    }

    private fun showFileList() {
        with(viewModel) {
            showFoldersInFileGroup(args.filesGroup)

            fileList.observe(viewLifecycleOwner) {
                filesByTypeAdapter.submitList(it)
            }
        }

    }



    companion object {
        private const val BINDING_EXCEPTION_MESSAGE = "FragmentFilesByTypeBinding = null"
    }
}