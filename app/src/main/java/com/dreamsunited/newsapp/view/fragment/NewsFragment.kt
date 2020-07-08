package com.dreamsunited.newsapp.view.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.dreamsunited.newsapp.NewsApplication
import com.dreamsunited.newsapp.databinding.FragmentNewsBinding
import com.dreamsunited.newsapp.model.Articles
import com.dreamsunited.newsapp.model.Source
import com.dreamsunited.newsapp.view.webview.URL
import com.dreamsunited.newsapp.view.webview.WebviewActivity
import com.dreamsunited.newsapp.view.fragment.adapter.NewsAdapter
import com.dreamsunited.newsapp.view.fragment.di.DaggerNewsFragmentComponent
import javax.inject.Inject

class NewsFragment(
    val source: Source
) : Fragment(), NewsAdapter.OnNewsAdapterItemClickListener {

    @Inject
    lateinit var vm : NewsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentNewsBinding.inflate(inflater)
        di()
        binding.apply {
            vm = vm
            lifecycleOwner = this@NewsFragment
        }
        vm.articles.observe(this, Observer {
            it?.let { articles ->
                binding.rvNewsItem.adapter = NewsAdapter(articles, this)
            }
        })
        vm.errorMessage.observe(this, Observer {
            it?.let {
                binding.tvError.text = it
            }
        })

        vm.isLoading.observe(this, Observer {
            it?.let {
                if(it) {
                    binding.loader.visibility = View.VISIBLE
                } else {
                    binding.loader.visibility = View.GONE
                }
            }
        })

        vm.isError.observe(this, Observer {
            it?.let {
                if(it) {
                    binding.tvError.visibility = View.VISIBLE
                    binding.rvNewsItem.visibility = View.GONE
                } else {
                    binding.tvError.visibility = View.GONE
                    binding.rvNewsItem.visibility = View.VISIBLE
                }
            }
        })
        vm.getAllNews(source = source.name)
        return binding.root
    }

    private fun di() {
        activity?.let {
            DaggerNewsFragmentComponent.factory()
                .create((it.application as NewsApplication).appComponent())
                .inject(this)
        }
    }

    override fun onClick(articles: Articles) {
        startActivity(Intent(activity, WebviewActivity::class.java).apply {
            putExtra(URL, articles.url)
        })
    }

    override fun onDestroy() {
        vm.onDestroy()
        super.onDestroy()
    }

}