package com.deanu.storyapp.widget

import android.content.Intent
import android.widget.RemoteViewsService
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StackWidgetService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent?): RemoteViewsFactory {
        return StackRemoteViewsFactory(
            this.application
        )
    }
}