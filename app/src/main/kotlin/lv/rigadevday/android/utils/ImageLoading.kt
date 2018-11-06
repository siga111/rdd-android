package lv.rigadevday.android.utils

import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.DrawableImageViewTarget
import com.bumptech.glide.request.target.Target
import lv.rigadevday.android.R

private fun ImageView.getFetcherInstance(url: String) = Glide
    .with(this.context)
    .load(url.toImageUrl())

private val defaultOptions get() = RequestOptions().diskCacheStrategy(DiskCacheStrategy.DATA)

fun ImageView.loadCircleAvatar(url: String): Target<Drawable> = getFetcherInstance(url)
    .apply(defaultOptions
        .centerCrop()
        .transform(CircleCrop())
    )
    .into(this)

fun ImageView.loadSquareAvatar(url: String): Target<Drawable> = getFetcherInstance(url)
    .apply(defaultOptions
        .centerCrop()
        .placeholder(R.drawable.vector_speaker_placeholder)
    )
    .into(this)

fun ImageView.loadLogo(url: String, onError: () -> Unit): Target<Drawable> = getFetcherInstance(url)
    .apply(defaultOptions.fitCenter())
    .into(object: DrawableImageViewTarget(this){
        override fun onLoadFailed(errorDrawable: Drawable?) {
            super.onLoadFailed(errorDrawable)
            onError()
        }
    })

fun ImageView.loadVenueImage(url: String): Target<Drawable> = getFetcherInstance(url)
    .apply(defaultOptions.centerCrop())
    .into(this)
