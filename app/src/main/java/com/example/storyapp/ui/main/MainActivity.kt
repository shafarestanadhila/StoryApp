package com.example.storyapp.ui.main

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.storyapp.R
import com.example.storyapp.adapter.StoryAdapter
import com.example.storyapp.adapter.StoryLoadingStateAdapter
import com.example.storyapp.databinding.ActivityMainBinding
import com.example.storyapp.ui.add.AddActivity
import com.example.storyapp.ui.login.LoginActivity
import com.example.storyapp.ui.maps.MapsActivity


class MainActivity : AppCompatActivity() {
    private val viewModel by viewModels<MainViewModel> {
        MainViewModelFactory.getInstance(this)
    }
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val adapter: StoryAdapter = StoryAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupAction()
        setupData(viewModel)

        binding.buttonAdd.setOnClickListener {
            val intent = Intent(this, AddActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.option_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu1 -> {
                val intent = Intent(Settings.ACTION_LOCALE_SETTINGS)
                startActivityForResult(intent, 0)
                true
            }
            R.id.menu2 -> {
                val intent = Intent(this, LoginActivity::class.java)
                viewModel.logout()
                startActivity(intent)
                true
            }
            R.id.menu3 -> {
                val intent = Intent(this, MapsActivity::class.java)
                startActivity(intent)
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        checkIfSessionValid()
    }

    private fun checkIfSessionValid() {
        viewModel.checkIfTokenAvailable().observe(this) {
            if (it == "null") {
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        }
    }

    private fun setupAction() {
        binding.rvStory.apply {
            adapter = this@MainActivity.adapter.withLoadStateFooter(
                footer = StoryLoadingStateAdapter {
                    retry()
                }
            )

            layoutManager = LinearLayoutManager(this@MainActivity)
            setHasFixedSize(true)
        }

        binding.swipeLayout.setOnRefreshListener {
            binding.tvError.visibility = View.INVISIBLE
            adapter.retry()
            adapter.refresh()
        }

        adapter.addLoadStateListener { loadState ->
            if (loadState.mediator?.refresh is LoadState.Loading) {
                if (adapter.snapshot().isEmpty()) {
                    binding.progressBar.visibility = View.VISIBLE
                }

                binding.tvEmptyStory.visibility = View.INVISIBLE
            } else {
                binding.progressBar.visibility = View.INVISIBLE
                binding.swipeLayout.isRefreshing = false

                val error = when {
                    loadState.mediator?.refresh is LoadState.Error -> loadState.mediator?.refresh as LoadState.Error
                    loadState.mediator?.prepend is LoadState.Error -> loadState.mediator?.prepend as LoadState.Error
                    loadState.mediator?.append is LoadState.Error -> loadState.mediator?.append as LoadState.Error
                    else -> null
                }

                error?.let {
                    if (adapter.snapshot().isEmpty()) {
                        Toast.makeText(this@MainActivity, getString(R.string.failed_fetching_data), Toast.LENGTH_SHORT).show()
                    }
                    binding.tvError.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun retry() {
        adapter.retry()
    }

    @OptIn(ExperimentalPagingApi::class)
    private fun setupData(viewModel: MainViewModel) {
        viewModel.getToken().observe(this) {
            if (it != "null") {
                getData(viewModel, "Bearer $it")
            }
        }
    }

    @OptIn(ExperimentalPagingApi::class)
    private fun getData(viewModel: MainViewModel, token: String) {
        viewModel.getStory(token).observe(this) {
            adapter.submitData(lifecycle, it)
        }
    }
}