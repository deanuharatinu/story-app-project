package com.deanu.storyapp.home

import android.Manifest
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.Settings
import android.view.*
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.app.ActivityCompat
import androidx.core.view.MenuProvider
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.RecyclerView
import com.deanu.storyapp.R
import com.deanu.storyapp.common.domain.model.BroadcastWidget
import com.deanu.storyapp.common.domain.model.Story
import com.deanu.storyapp.common.utils.REQUEST_CODE_PERMISSIONS
import com.deanu.storyapp.common.utils.isPermissionGranted
import com.deanu.storyapp.databinding.FragmentHomeBinding
import com.deanu.storyapp.databinding.ItemStoryBinding
import com.deanu.storyapp.widget.StackViewWidget
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private val viewModel: HomeViewModel by viewModels()
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: StoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Checking camera permission
        if (!isPermissionGranted(requireContext(), arrayOf(Manifest.permission.CAMERA))) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.CAMERA),
                REQUEST_CODE_PERMISSIONS
            )
        }

        initToolbar()
        initMenu()
        initRecyclerView()
        initIsLoadingObserver()
        initStoryList()
        initListener()
    }

    private fun scrollToPosition() {
        binding.rvStory.addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
            override fun onLayoutChange(
                p0: View?,
                p1: Int,
                p2: Int,
                p3: Int,
                p4: Int,
                p5: Int,
                p6: Int,
                p7: Int,
                p8: Int
            ) {
                binding.rvStory.removeOnLayoutChangeListener(this)

                if (binding.rvStory.layoutManager != null) {
                    val layoutManager: RecyclerView.LayoutManager = binding.rvStory.layoutManager!!
                    val viewAtPosition =
                        layoutManager.findViewByPosition(viewModel.getAdapterPosition())
                    if (viewAtPosition == null || layoutManager
                            .isViewPartiallyVisible(viewAtPosition, false, true)
                    ) {
                        val position = viewModel.getAdapterPosition()
                        binding.rvStory.post {
                            layoutManager.scrollToPosition(
                                if (position == -1) 0 else position
                            )
                        }
                    }
                }
            }
        })
    }

    private fun initMenu() {
        binding.toolbar.addMenuProvider(
            object : MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.menu_home, menu)
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                    return when (menuItem.itemId) {
                        R.id.choose_language -> {
                            startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                            true
                        }
                        R.id.open_maps -> {
                            view?.findNavController()?.navigate(R.id.mapsFragment)
                            true
                        }
                        R.id.logout -> {
                            viewModel.logout()
                            true
                        }
                        else -> false
                    }
                }

            },
            viewLifecycleOwner,
            Lifecycle.State.RESUMED
        )
    }

    override fun onResume() {
        super.onResume()
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            viewModel.incrementLogoutCounter()
        }

        if (viewModel.getAdapterPosition() == -1) {
            scrollToPosition()
        }
    }

    private fun initToolbar() {
        binding.tvToolbarTitle.text = getString(R.string.home_title)
    }

    private fun initIsLoadingObserver() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.loading.visibility = if (isLoading) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
    }

    private fun initStoryList() {
        viewModel.token.observe(viewLifecycleOwner) { token ->
            if (!token.isNullOrEmpty()) {
                showStoryList(token)
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.have_been_logged_out),
                    Toast.LENGTH_SHORT
                ).show()
                view?.findNavController()?.popBackStack()
                view?.findNavController()?.navigate(R.id.loginFragment)
            }
        }

        viewModel.storyList.observe(viewLifecycleOwner) { storyList ->
            if (storyList.isNotEmpty()) {
                // Send broadcast
                sendBroadcastToWidget(storyList)
            }
        }
    }

    private fun showStoryList(token: String) {
        viewModel.getStoryList(token)
        val adapterPosition = viewModel.getAdapterPosition()
        viewModel.storyListWithPaging(
            token,
            adapterPosition == -1
        ).observe(viewLifecycleOwner) {
            adapter.submitData(viewLifecycleOwner.lifecycle, it)
        }
    }

    private fun sendBroadcastToWidget(storyList: List<Story>) {
        if (storyList.isNotEmpty()) {
            val broadcastWidget = BroadcastWidget(storyList)
            val intent = Intent(requireContext(), StackViewWidget::class.java)
            intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            val ids = AppWidgetManager.getInstance(requireContext())
                .getAppWidgetIds(ComponentName(requireContext(), StackViewWidget::class.java))
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
            intent.putExtra(StackViewWidget.EXTRA_DATA, broadcastWidget)
            requireActivity().sendBroadcast(intent)
        }
    }

    private fun initRecyclerView() {
        adapter = StoryAdapter(viewModel) { story, binding ->
            navigateToStoryDetail(story, binding)
        }
        adapter.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        binding.rvStory.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                adapter.retry()
            }
        )

        postponeEnterTransition()
        (view?.parent as? ViewGroup)?.doOnPreDraw {
            startPostponedEnterTransition()
        }

        scrollToPosition()
    }

    private fun navigateToStoryDetail(story: Story, binding: ItemStoryBinding) {
        val extras = FragmentNavigatorExtras(
            binding.tvUsername to story.id
        )
        val action = HomeFragmentDirections.actionHomeFragmentToDetailStoryFragment(
            story.id,
            story.name,
            story.photoUrl,
            story.description
        )
        view?.findNavController()?.navigate(
            action,
            extras
        )
    }

    private fun initListener() {
        binding.fabAddStory.setOnClickListener {
            viewModel.setAdapterPosition(-1)
            view?.findNavController()?.navigate(R.id.addStoryFragment)
        }

        viewModel.backPressCounter.observe(viewLifecycleOwner) { counter ->
            if (counter == 1) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.pressback_once_again),
                    Toast.LENGTH_SHORT
                ).show()

                object : CountDownTimer(2000, 1000) {
                    override fun onTick(p0: Long) {
                        // Nothing
                    }

                    override fun onFinish() {
                        viewModel.resetBackPressCounter()
                    }
                }.start()
            } else if (counter == 2) {
                viewModel.logout()
            }
        }

        viewModel.responseMessage.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.resetBackPressCounter()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        viewModel.setAdapterPosition(-1)
    }
}