package com.dreamsunited.newsapp.view.loadable

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.dreamsunited.newsapp.R
import com.dreamsunited.newsapp.databinding.LayoutLoadableImageviewBinding

class LoadableImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    val binding: LayoutLoadableImageviewBinding = LayoutLoadableImageviewBinding.inflate(
        LayoutInflater.from(context),
        this,
        true
    ).also { it.isLoading = true }

    var loadUri: String? = null
        set(value) = synchronized(this) {
            field = value
            if (value.isNullOrBlank()) {
                visibility = View.GONE
                return
            } else {
                visibility = View.VISIBLE
            }
            binding.isLoading = true
            Glide.with(this)
                .load(value)
                .fallback(R.drawable.ic_loading_info)
                .listener(object : RequestListener<Drawable> {

                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        binding.isLoading = false
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        binding.isLoading = false
                        return false
                    }
                })
                .into(binding.imagePreview)
        }
}