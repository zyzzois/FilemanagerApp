package com.example.filemanager.ui.screens

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.domain.entity.FileGroup
import com.example.filemanager.R
import com.example.filemanager.app.FileManagerApp
import com.example.filemanager.databinding.FragmentFilesByTypeBinding
import com.example.filemanager.ui.recycler.FileListAdapter
import com.example.filemanager.ui.vm.FilesByTypeViewModel
import com.example.filemanager.ui.vm.ViewModelFactory
import com.example.filemanager.utils.Constants.FRAGMENT_FILES_BY_TYPE_BINDING_IS_NULL
import com.example.filemanager.utils.FileOpener
import com.example.filemanager.utils.showToast
import com.google.android.material.bottomsheet.BottomSheetBehavior
import javax.inject.Inject

class FilesByTypeFragment : Fragment(), MenuProvider {

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

    private lateinit var bottomSheetBehaviorActions: BottomSheetBehavior<LinearLayout>
    private lateinit var bottomSheetBehaviorRename: BottomSheetBehavior<LinearLayout>

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
        setupLongClickListener()
        setupBottomSheet()
        activity?.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun setTextViewVisible() {
        binding.textViewWait.visibility = View.VISIBLE
    }

    private fun setupBottomSheet() {
        bottomSheetBehaviorActions = BottomSheetBehavior.from(binding.bottomMenuActions.bottomActions)
        bottomSheetBehaviorRename = BottomSheetBehavior.from(binding.bottomMenuRename.bottomMenu)
        bottomSheetBehaviorActions.peekHeight = 0
        bottomSheetBehaviorRename.peekHeight = 0
        bottomSheetBehaviorActions.state = BottomSheetBehavior.STATE_COLLAPSED
        bottomSheetBehaviorRename.state = BottomSheetBehavior.STATE_COLLAPSED
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

    private fun setupLongClickListener() = with(binding) {
        filesByTypeAdapter.onFileItemLongClickListener = {
            val selectedFile = it
            bottomSheetBehaviorActions.state = BottomSheetBehavior.STATE_EXPANDED
            bottomSheetBackGround.visibility = View.VISIBLE

            bottomSheetBackGround.setOnClickListener {
                bottomSheetBehaviorActions.state = BottomSheetBehavior.STATE_COLLAPSED
                bottomSheetBehaviorRename.state = BottomSheetBehavior.STATE_COLLAPSED
                bottomSheetBackGround.visibility = View.GONE
            }

            bottomMenuRename.buttonDeleteOnBottomMenu.setOnClickListener {
                bottomSheetBehaviorActions.state = BottomSheetBehavior.STATE_COLLAPSED
                bottomSheetBehaviorRename.state = BottomSheetBehavior.STATE_COLLAPSED
                bottomSheetBackGround.visibility = View.GONE
            }

            bottomMenuActions.buttonDelete.setOnClickListener {
                val builder = context?.let { it1 -> AlertDialog.Builder(it1) }
                builder?.setTitle(requireContext().getString(R.string.question))
                builder?.setPositiveButton(requireContext().getString(R.string.yes)) { _, _ ->
                    viewModel.deleteFile(selectedFile)
                    bottomSheetBehaviorActions.state = BottomSheetBehavior.STATE_COLLAPSED
                    bottomSheetBackGround.visibility = View.GONE
                }
                builder?.setNegativeButton(requireContext().getString(R.string.cancel)) { _, _ ->
                    bottomSheetBehaviorActions.state = BottomSheetBehavior.STATE_COLLAPSED
                    bottomSheetBackGround.visibility = View.GONE
                }
                val dialog = builder?.create()
                dialog?.show()
            }

            bottomMenuActions.buttonRename.setOnClickListener {
                bottomSheetBackGround.visibility = View.GONE
                bottomSheetBehaviorActions.state = BottomSheetBehavior.STATE_COLLAPSED
                bottomSheetBehaviorRename.state = BottomSheetBehavior.STATE_EXPANDED
                bottomSheetBackGround.visibility = View.VISIBLE
                bottomMenuRename.inputFileName.setText(selectedFile.filename)
                //TODO(editor file mode)
            }
            bottomMenuActions.buttonShare.setOnClickListener {
                bottomSheetBackGround.visibility = View.GONE
                bottomSheetBehaviorActions.state = BottomSheetBehavior.STATE_COLLAPSED
                FileOpener().shareFile(requireContext(), selectedFile.file)
            }
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
        if (viewModel.fileList.value?.isEmpty() == true)
            setTextViewVisible()
    }
    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menu.clear()
        menuInflater.inflate(R.menu.sort_menu2, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.sortByName -> viewModel.fileList.observe(viewLifecycleOwner) { it ->
                if (it != null) filesByTypeAdapter.submitList(it.sortedBy { it.filename })
            }
            R.id.sortByDate -> viewModel.fileList.observe(viewLifecycleOwner) { it ->
                if (it != null) filesByTypeAdapter.submitList(it.sortedByDescending { it.timestamp })
            }
            R.id.sortByFromLargestToSmallest -> viewModel.fileList.observe(viewLifecycleOwner) { it ->
                if (it != null) filesByTypeAdapter.submitList(it.sortedByDescending { it.fileSize })
            }
            android.R.id.home -> findNavController().navigateUp()
            else -> {
                showToast("menu")
            }
        }
        return true
    }
}