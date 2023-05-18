package com.example.filemanager.ui.screens

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.domain.entity.FileGroup
import com.example.filemanager.R
import com.example.filemanager.app.FileManagerApp
import com.example.filemanager.databinding.FragmentHomeBinding
import com.example.filemanager.ui.recycler.FileListAdapter
import com.example.filemanager.ui.vm.HomeViewModel
import com.example.filemanager.ui.vm.ViewModelFactory
import com.example.filemanager.utils.Constants.HOME_FRAGMENT_BINDING_IS_NULL
import com.example.filemanager.utils.FileOpener
import com.google.android.material.bottomsheet.BottomSheetBehavior
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

    private lateinit var bottomSheetBehaviorActions: BottomSheetBehavior<LinearLayout>
    private lateinit var bottomSheetBehaviorRename: BottomSheetBehavior<LinearLayout>

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
        setTextViewVisible()
        initButtons()
        init()
        setupBottomSheet()
        showRecentUpdatedFileList()
        setupClickListener()

        setupLongClickListener()
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

    private fun setupLongClickListener() = with(binding) {
        recentUpdatedListAdapter.onFileItemLongClickListener = {
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

            }
            bottomMenuActions.buttonShare.setOnClickListener {
                bottomSheetBackGround.visibility = View.GONE
                bottomSheetBehaviorActions.state = BottomSheetBehavior.STATE_COLLAPSED
                FileOpener().shareFile(requireContext(), selectedFile.file)

            }
        }
    }

    private fun setupClickListener() {
        recentUpdatedListAdapter.onFileItemClickListener = {
            FileOpener().openFile(requireContext(), it.file)
        }
    }

    private fun showRecentUpdatedFileList() {
        viewModel.findLastModifiedFileList()
        viewModel.lastModifiedFileList.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.textViewWait.visibility = View.GONE
                recentUpdatedListAdapter.submitList(it)
            }
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
            buttonZipFiles.setOnClickListener {
                findNavController().navigate(
                    HomeFragmentDirections.actionNavHomeToFilesByTypeFragment(
                        FileGroup.ARCHIVES
                    ).setFilesGroup(FileGroup.ARCHIVES)
                )
            }
            buttonDownloadFiles.setOnClickListener {
                findNavController().navigate(
                    HomeFragmentDirections.actionNavHomeToFilesByTypeFragment(
                        FileGroup.DOWNLOADS
                    ).setFilesGroup(FileGroup.DOWNLOADS)
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