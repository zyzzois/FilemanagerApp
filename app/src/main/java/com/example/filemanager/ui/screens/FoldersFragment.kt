package com.example.filemanager.ui.screens

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.filemanager.app.FileManagerApp
import com.example.filemanager.databinding.FragmentFoldersBinding
import com.example.filemanager.ui.recycler.FileListAdapter
import com.example.filemanager.utils.FileOpener
import com.example.filemanager.ui.vm.FoldersViewModel
import com.example.filemanager.ui.vm.ViewModelFactory
import javax.inject.Inject

class FoldersFragment : Fragment() {

    private val args by navArgs<FoldersFragmentArgs>()

    private val component by lazy { (requireActivity().application as FileManagerApp).component }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel by lazy {
        ViewModelProvider(requireActivity(), viewModelFactory)[FoldersViewModel::class.java]
    }

    private var _binding: FragmentFoldersBinding? = null
    private val binding: FragmentFoldersBinding
        get() = _binding ?: throw RuntimeException(BINDING_EXCEPTION_MESSAGE)

    private lateinit var foldersAdapter: FileListAdapter

    override fun onAttach(context: Context) {
        component.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFoldersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        showFolderList()
        setupClickListener()
        setupLongClickListener()
    }

    private fun initRecyclerView() {
        with(binding) {
            rcView.layoutManager = LinearLayoutManager(activity)
            foldersAdapter = FileListAdapter(requireContext())
            rcView.adapter = foldersAdapter
        }
    }

    private fun showFolderList() {
        with(viewModel) {
            currentPath.observe(viewLifecycleOwner) {
                binding.tvPath.text = it
            }
            updateList(args.path)
            folderList.observe(viewLifecycleOwner) {
                foldersAdapter.submitList(it)
            }
        }
    }

    private fun setupClickListener() {
        foldersAdapter.onFileItemClickListener = {
            if (it.file.isDirectory) {
                val path = it.file.absolutePath
                findNavController().navigate(
                    FoldersFragmentDirections.actionNavFoldersSelf().setPath(path)
                )
                viewModel.setPath(path)
            }
            else FileOpener().openFile(requireContext(), it.file)

        }
    }

    private fun setupLongClickListener() {
        foldersAdapter.onFileItemLongClickListener = {

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ERROR_MSG = "Содержимое этой папки " +
                "не может быть отображено из-за ограничений Android"
        private const val BINDING_EXCEPTION_MESSAGE = "FragmentFoldersBinding = null"
    }
}