package com.example.filemanager.ui.screens

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.domain.entity.FileEntity
import com.example.filemanager.R
import com.example.filemanager.app.FileManagerApp
import com.example.filemanager.databinding.FragmentFoldersBinding
import com.example.filemanager.ui.recycler.FileListAdapter
import com.example.filemanager.utils.FileOpener
import com.example.filemanager.ui.vm.FoldersViewModel
import com.example.filemanager.ui.vm.ViewModelFactory
import com.example.filemanager.utils.Constants.FRAGMENT_FOLDERS_BINDING_IS_NULL
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.vk.api.sdk.VK
import com.vk.api.sdk.auth.VKAuthenticationResult
import javax.inject.Inject

class FoldersFragment : Fragment() {

    private val args by navArgs<FoldersFragmentArgs>()

    private val component by lazy {
        (requireActivity().application as FileManagerApp).component
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel by lazy {
        ViewModelProvider(requireActivity(), viewModelFactory)[FoldersViewModel::class.java]
    }

    private var _binding: FragmentFoldersBinding? = null
    private val binding: FragmentFoldersBinding
        get() = _binding ?: throw RuntimeException(FRAGMENT_FOLDERS_BINDING_IS_NULL)

    private lateinit var foldersAdapter: FileListAdapter

    private lateinit var bottomSheetBehaviorActions: BottomSheetBehavior<LinearLayout>
    private lateinit var bottomSheetBehaviorRename: BottomSheetBehavior<LinearLayout>


    private val mainPopupMenu by lazy {
        PopupMenu(context, binding.btnSorting)
    }

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
        init()
        showFolderList()
        setupClickListener()
        setupLongClickListener()
        setupBottomSheet()
        setupMainPopUp()
    }

    private fun setupBottomSheet() {
        bottomSheetBehaviorActions = BottomSheetBehavior.from(binding.bottomMenuActions.bottomActions)
        bottomSheetBehaviorRename = BottomSheetBehavior.from(binding.bottomMenuRename.bottomMenu)
        bottomSheetBehaviorActions.peekHeight = 0
        bottomSheetBehaviorRename.peekHeight = 0
        bottomSheetBehaviorActions.state = BottomSheetBehavior.STATE_COLLAPSED
        bottomSheetBehaviorRename.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    private fun init() {
        with(binding) {
            rcView.layoutManager = LinearLayoutManager(activity)
            foldersAdapter = FileListAdapter(requireContext())
            rcView.adapter = foldersAdapter
            registerForContextMenu(btnSorting)
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
                if (viewModel.isAccessiblePath(path)) {
                    viewModel.setPath(path)
                    findNavController().navigate(
                        FoldersFragmentDirections.actionNavFoldersSelf().setPath(path)
                    )
                } else {
                    Toast.makeText(
                        context,
                        context?.getString(R.string.folder_inaccessible),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            else {
                viewModel.saveCheckedFileToDB(it)
                FileOpener().openFile(requireContext(), it.file)
            }
        }
    }

    private fun setupLongClickListener() = with(binding) {
        foldersAdapter.onFileItemLongClickListener = {
            val selectedFile = it
            bottomSheetBehaviorActions.state = BottomSheetBehavior.STATE_EXPANDED
            bottomSheetBackGround.visibility = View.VISIBLE

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

            }
        }
    }

    private fun setupMainPopUp() = with(mainPopupMenu) {
        binding.btnSorting.setOnClickListener {
            menu.clear()
            menuInflater.inflate(R.menu.sort_menu, menu)
            setOnMenuItemClickListener { item ->
                if (item != null) {
                    when (item.itemId) {
                        R.id.sortByName -> {
                            viewModel.folderList.observe(viewLifecycleOwner) { it ->
                                if (it != null)
                                    foldersAdapter.submitList(it.sortedBy { it.filename })
                            }
                        }
                        R.id.sortByDate -> {
                            viewModel.folderList.observe(viewLifecycleOwner) { it ->
                                if (it != null)
                                    foldersAdapter.submitList(it.sortedBy { it.timestamp })
                            }
                        }
                        R.id.sortByFromLargestToSmallest -> {
                            viewModel.folderList.observe(viewLifecycleOwner) { it ->
                                if (it != null)
                                    foldersAdapter.submitList(it.sortedBy { it.fileSize })
                            }
                        }
                    }
                }
                true
            }
            show()
        }
    }

//    private fun setupOnBackPressed() {
//        activity?.onBackPressedDispatcher?.addCallback(
//            viewLifecycleOwner,
//            object : OnBackPressedCallback(true) {
//                override fun handleOnBackPressed() {
//
//                }
//        })
//    }

}