package com.dreamsunited.newsapp.view.fragment.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dreamsunited.newsapp.databinding.ItemNewsBinding
import com.dreamsunited.newsapp.model.Articles

class NewsAdapter(private var articleList : List<Articles>,
                  private val listener: OnNewsAdapterItemClickListener) : RecyclerView.Adapter<NewsAdapter.Holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(ItemNewsBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun getItemCount(): Int = articleList.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val articles = articleList[position]
        holder.bind(articles)
        holder.itemView.setOnClickListener {
            listener.onClick(articles)
        }
    }

    class Holder(private var binding: ItemNewsBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(articles: Articles) {
            binding.articles = articles
            binding.executePendingBindings()
        }
    }

    fun submitList(data: List<Articles>?) {
        data?.let {
            articleList = it
            notifyDataSetChanged()
        }
    }

    interface OnNewsAdapterItemClickListener {
        fun onClick(articles: Articles)
    }
}