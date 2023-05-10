package com.example.filemanager.ui.screens

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.filemanager.R
import com.example.filemanager.app.FileManagerApp
import com.example.filemanager.databinding.FragmentFoldersBinding
import com.example.filemanager.ui.recycler.FileListAdapter
import com.example.filemanager.utils.FileOpener
import com.example.filemanager.ui.vm.FoldersViewModel
import com.example.filemanager.ui.vm.ViewModelFactory
import com.example.filemanager.utils.Constants.FRAGMENT_FOLDERS_BINDING_IS_NULL
import com.google.android.material.bottomsheet.BottomSheetBehavior
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

    private val bottomSheetRenameMenu by lazy {
        BottomSheetBehavior.from(binding.bottomMenuId.bottomMenu)
    }

    private val bottomSheetActionsMenu by lazy {
        BottomSheetBehavior.from(binding.bottomMenuActions.bottomActions)
    }

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
        hideBottomMenus()
        init()
        showFolderList()
        setupClickListener()
        setupLongClickListener()
        //setupOnBackPressed()
        setupMainPopUp()
    }

    private fun hideBottomMenus() {
        bottomSheetRenameMenu.apply {
            peekHeight = 0
            state = BottomSheetBehavior.STATE_HIDDEN
        }
        bottomSheetActionsMenu.apply {
            peekHeight = 0
            state = BottomSheetBehavior.STATE_HIDDEN
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

    private fun init() {
        with(binding) {
            rcView.layoutManager = LinearLayoutManager(activity)
            foldersAdapter = FileListAdapter(requireContext())
            rcView.adapter = foldersAdapter
            registerForContextMenu(btnSorting)
        }
        bottomSheetRenameMenu.apply {
            peekHeight = 0
            state = BottomSheetBehavior.STATE_HIDDEN
        }
        bottomSheetActionsMenu.apply {
            peekHeight = 0
            state = BottomSheetBehavior.STATE_COLLAPSED
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
            else FileOpener().openFile(requireContext(), it.file)
        }
    }

    private fun setupLongClickListener() = with(binding) {
        foldersAdapter.onFileItemLongClickListener = {
            val selectedFile = it
            bottomSheetActionsMenu.state = BottomSheetBehavior.STATE_EXPANDED
            bottomSheetBackGround.visibility = View.VISIBLE

            bottomMenuActions.buttonDelete.setOnClickListener {
                val builder = context?.let { it1 -> AlertDialog.Builder(it1) }
                builder?.setTitle("Вы действительно хотите удалить файл?")
                builder?.setPositiveButton("Да") { _, _ ->
                    viewModel.deleteFile(selectedFile)
                    //TODO(update list after deleting)

                    bottomSheetActionsMenu.state = BottomSheetBehavior.STATE_HIDDEN
                    bottomSheetBackGround.visibility = View.INVISIBLE
                }
                builder?.setNegativeButton("Отмена") { _, _ ->
                    bottomSheetActionsMenu.state = BottomSheetBehavior.STATE_HIDDEN
                    bottomSheetBackGround.visibility = View.INVISIBLE
                }
                val dialog = builder?.create()
                dialog?.show()
            }
            bottomMenuActions.buttonRename.setOnClickListener {
                bottomSheetRenameMenu.state = BottomSheetBehavior.STATE_EXPANDED
                bottomSheetBackGround.visibility = View.VISIBLE


            }

//            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
//            binding.bottomSheetBackGround.visibility = View.VISIBLE
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


    override fun onResume() {
        hideBottomMenus()
        super.onResume()
    }

    override fun onStart() {
        hideBottomMenus()
        super.onStart()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}