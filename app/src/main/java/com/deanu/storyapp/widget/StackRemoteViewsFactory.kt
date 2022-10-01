package com.deanu.storyapp.widget

import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import androidx.core.os.bundleOf
import com.bumptech.glide.Glide
import com.deanu.storyapp.R
import com.deanu.storyapp.common.domain.model.Story

internal class StackRemoteViewsFactory(
    private val appContext: Context
) : RemoteViewsService.RemoteViewsFactory {
    private var itemList = ArrayList<Story>()

    override fun onCreate() {
        // Empty
    }

    override fun onDataSetChanged() {
        if (mWidgetItems.isNotEmpty()) {
            itemList.clear()
            itemList.addAll(mWidgetItems)
        }
    }

    override fun onDestroy() {
        // Empty
    }

    override fun getCount(): Int = mWidgetItems.size

    override fun getViewAt(position: Int): RemoteViews {
        val rv = RemoteViews(appContext.packageName, R.layout.item_widget).apply {
            val image = Glide.with(appContext)
                .asBitmap()
                .load(mWidgetItems[position].photoUrl)
                .submit(512, 512)
                .get()
            setImageViewBitmap(R.id.imageView, image)
        }

        val extras = bundleOf(
            StackViewWidget.EXTRA_ITEM to position
        )
        val fillInIntent = Intent()
        fillInIntent.putExtras(extras)
        rv.setOnClickFillInIntent(R.id.imageView, fillInIntent)
        return rv
    }

    override fun getLoadingView(): RemoteViews? = null

    override fun getViewTypeCount(): Int = 1

    override fun getItemId(p0: Int): Long = 0

    override fun hasStableIds(): Boolean = false

    companion object {
        var mWidgetItems = ArrayList<Story>()
    }
}