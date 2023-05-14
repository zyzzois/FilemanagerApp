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
import android.widget.PopupMenu
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.util.query
import com.example.filemanager.R
import com.example.filemanager.app.FileManagerApp
import com.example.filemanager.databinding.FragmentFoldersBinding
import com.example.filemanager.ui.recycler.FileListAdapter
import com.example.filemanager.ui.vm.FoldersViewModel
import com.example.filemanager.ui.vm.ViewModelFactory
import com.example.filemanager.utils.Constants.FOLDER_INACCESSIBLE
import com.example.filemanager.utils.Constants.FRAGMENT_FOLDERS_BINDING_IS_NULL
import com.example.filemanager.utils.Constants.SEARCH
import com.example.filemanager.utils.FileOpener
import com.example.filemanager.utils.showToast
import com.google.android.material.bottomsheet.BottomSheetBehavior
import javax.inject.Inject
import kotlin.math.log

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
        setupSearchViewField()
    }

    private fun setupSearchViewField() {
        (requireActivity() as MenuHost).addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.main, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.search -> {
                        val onActionExpandListener: MenuItem.OnActionExpandListener = object : MenuItem.OnActionExpandListener {
                            override fun onMenuItemActionExpand(item: MenuItem): Boolean { return true }
                            override fun onMenuItemActionCollapse(item: MenuItem): Boolean { return true }
                        }
                        menuItem.setOnActionExpandListener(onActionExpandListener)
                        val searchView = menuItem.actionView as SearchView
                        searchView.queryHint = SEARCH

                        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
                            override fun onQueryTextSubmit(query: String?): Boolean {
                                if (query != null)
                                    viewModel.searchInCurrentList(query)
                                viewModel.searchedQueryList.observe(viewLifecycleOwner) {
                                    foldersAdapter.submitList(it)
                                }
                                return true
                            }
                            override fun onQueryTextChange(newText: String?): Boolean {
                                view?.let {
                                    if (newText != null)
                                        viewModel.searchInCurrentList(newText)
                                    viewModel.searchedQueryList.observe(viewLifecycleOwner) {
                                        foldersAdapter.submitList(it)
                                    }
                                }
                                return true
                            }
                        })
                        menuItem.expandActionView()
                    }
                    else -> {
                        val drawerLayout = requireActivity().findViewById<DrawerLayout>(R.id.drawer_layout)
                        drawerLayout.openDrawer(GravityCompat.START)
                    }
                }
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
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
            updateList(args.path)
            currentPath.observe(viewLifecycleOwner) {
                binding.tvPath.text = it
            }
            folderList.observe(viewLifecycleOwner) {
                foldersAdapter.submitList(it)
            }
        }
    }

    private fun setupClickListener() {
        foldersAdapter.onFileItemClickListener = {
            if (it.file.isDirectory) {
                if (it.file.canRead()) {
                    findNavController().navigate(
                        FoldersFragmentDirections.actionNavFoldersSelf().setPath(it.file.path)
                    )
                }
                else showToast(FOLDER_INACCESSIBLE)
            }
            else FileOpener().openFile(requireContext(), it.file)
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
                FileOpener().shareFile(requireContext(), selectedFile.file)

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

}