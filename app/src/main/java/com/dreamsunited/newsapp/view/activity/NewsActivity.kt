package com.dreamsunited.newsapp.view.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.dreamsunited.newsapp.NewsApplication
import com.dreamsunited.newsapp.R
import com.dreamsunited.newsapp.databinding.ActivityNewsBinding
import com.dreamsunited.newsapp.view.activity.adapter.NewsSourceAdapter
import com.dreamsunited.newsapp.view.activity.di.DaggerNewsActivityComponent
import javax.inject.Inject

class NewsActivity : AppCompatActivity() {

    @Inject
    lateinit var vm: NewsSourceViewModel

    lateinit var adapter: NewsSourceAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityNewsBinding>(this, R.layout.activity_news)
        di()
        setSupportActionBar(binding.toolbar)
        binding?.apply {
            vm = this.vm
            lifecycleOwner = this@NewsActivity
        }
        vm.loaderVisibility.observe(this, Observer {
            it.let {
                binding.loader.visibility = it
            }
        })
        vm.sources.observe(this, Observer {
            it?.let {
                adapter = NewsSourceAdapter(this.supportFragmentManager, it)
                binding.container.adapter = adapter
                binding.tabNews.setupWithViewPager(binding.container)
            }
        })
    }

    private fun di() {
        DaggerNewsActivityComponent.factory()
            .create((application as NewsApplication).appComponent())
            .inject(this)
    }

    override fun onDestroy() {
        vm.onDestroy()
        super.onDestroy()
    }
}