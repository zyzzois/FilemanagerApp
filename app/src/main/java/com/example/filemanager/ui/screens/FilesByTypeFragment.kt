package com.example.filemanager.ui.screens

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.domain.entity.FileGroup
import com.example.filemanager.app.FileManagerApp
import com.example.filemanager.databinding.FragmentFilesByTypeBinding
import com.example.filemanager.ui.recycler.FileListAdapter
import com.example.filemanager.ui.vm.FilesByTypeViewModel
import com.example.filemanager.ui.vm.ViewModelFactory
import com.example.filemanager.utils.Constants.FRAGMENT_FILES_BY_TYPE_BINDING_IS_NULL
import com.example.filemanager.utils.FileOpener
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
        get() = _binding ?: throw RuntimeException(FRAGMENT_FILES_BY_TYPE_BINDING_IS_NULL)

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
        setTextViewVisible()
        updateTitle()
        initRecyclerView()
        showFileList()
        setupClickListener()
    }

    private fun setTextViewVisible() {
        binding.textViewWait.visibility = View.VISIBLE
    }

    private fun updateTitle() {
        val actionBar = (requireActivity() as AppCompatActivity).supportActionBar
        when (args.filesGroup) {
            FileGroup.DOWNLOADS -> actionBar?.title = "Загрузки"
            FileGroup.IMAGES -> actionBar?.title = "Фото"
            FileGroup.ARCHIVES ->  actionBar?.title = "Архивы"
            FileGroup.VIDEOS ->  actionBar?.title = "Видео"
            FileGroup.AUDIO ->  actionBar?.title = "Музыка"
            FileGroup.APK ->  actionBar?.title = "APK"
            FileGroup.DOCUMENTS ->  actionBar?.title = "Документы"
            else -> actionBar?.title = "Files"
        }
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
            showFilesInSelectedGroup(args.filesGroup)
            fileList.observe(viewLifecycleOwner) {
                binding.textViewWait.visibility = View.GONE
                filesByTypeAdapter.submitList(it)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.clearList()
    }

    override fun onResume() {
        super.onResume()
        setTextViewVisible()
    }
}