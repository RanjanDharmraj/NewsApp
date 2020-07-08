package com.dreamsunited.newsapp.view.activity.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.dreamsunited.newsapp.model.Source
import com.dreamsunited.newsapp.view.fragment.NewsFragment

class NewsSourceAdapter(
    fm: FragmentManager,
    private var sources: List<Source>
) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        return NewsFragment(sources[position])
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return sources[position].name
    }

    override fun getCount(): Int = sources.size

}