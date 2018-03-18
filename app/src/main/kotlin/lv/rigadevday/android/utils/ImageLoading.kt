package lv.rigadevday.android.utils

import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.target.ViewTarget
import com.bumptech.glide.request.transition.Transition
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import lv.rigadevday.android.R
import lv.rigadevday.android.utils.glide.GlideApp

private val storageRef: StorageReference by lazy {
    FirebaseStorage.getInstance().reference
}

private fun ImageView.getFetcherInstance() = GlideApp.with(this.context)

fun String.toImageUrl() =
    if (this.startsWith("http")) this
    else "http://rigadevdays.lv${this.replace("..", "")}"

private val defaultOptions get() = RequestOptions().diskCacheStrategy(DiskCacheStrategy.DATA)

fun ImageView.loadCircleAvatar(url: String): Target<Drawable> = getFetcherInstance()
    .load(storageRef.child(url))
    .apply(defaultOptions
        .centerCrop()
        .transform(CircleCrop())
    )
    .into(this)

fun ImageView.loadSquareAvatar(url: String) = getFetcherInstance()
    .load(storageRef.child(url))
    .apply(defaultOptions
        .centerCrop()
        .placeholder(R.drawable.vector_speaker_placeholder)
    )
    .into(this)

fun ImageView.loadLogo(url: String, onDone: () -> Unit): Target<Drawable> = getFetcherInstance()
    .load(url.toImageUrl())
    .apply(defaultOptions.fitCenter())
    .into(object : ViewTarget<ImageView, Drawable>(this) {
        override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
            view.setImageDrawable(resource)
            onDone()
        }
    })

fun ImageView.loadVenueImage(url: String): Target<Drawable> = getFetcherInstance()
    .load(storageRef.child(url))
    .apply(defaultOptions.centerCrop())
    .into(this)
