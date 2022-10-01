package com.deanu.storyapp.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import androidx.core.net.toUri
import com.deanu.storyapp.R
import com.deanu.storyapp.common.domain.model.BroadcastWidget

class StackViewWidget : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.getSerializableExtra(EXTRA_DATA) != null) {
            val broadcastWidget: BroadcastWidget =
                intent.getSerializableExtra(EXTRA_DATA) as BroadcastWidget

            StackRemoteViewsFactory.mWidgetItems = arrayListOf()
            broadcastWidget.storyList.map {
                StackRemoteViewsFactory.mWidgetItems.add(it)
            }

            val intent2 = Intent(context, StackWidgetService::class.java)
            intent2.data = intent2.toUri(Intent.URI_INTENT_SCHEME).toUri()
            val views = RemoteViews(context.packageName, R.layout.stack_view_widget)
            views.setRemoteAdapter(R.id.stack_view, intent2)
            views.setEmptyView(R.id.stack_view, R.id.empty_view)
            val manager = AppWidgetManager.getInstance(context)
            manager.updateAppWidget(ComponentName(context, StackViewWidget::class.java), views)
        }
    }

    companion object {
        private const val TOAST_ACTION = "TOAST_ACTION"
        const val EXTRA_ITEM = "EXTRA_ITEM"
        const val EXTRA_DATA = "EXTRA_DATA"

        private fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val intent = Intent(context, StackWidgetService::class.java)
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            intent.data = intent.toUri(Intent.URI_INTENT_SCHEME).toUri()

            val views = RemoteViews(context.packageName, R.layout.stack_view_widget)
            views.setRemoteAdapter(R.id.stack_view, intent)
            views.setEmptyView(R.id.stack_view, R.id.empty_view)
            val toastIntent = Intent(context, StackViewWidget::class.java)
            toastIntent.action = TOAST_ACTION
            toastIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)

            val toastPendingIntent = PendingIntent.getBroadcast(
                context, 0, toastIntent,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
                else 0
            )
            views.setPendingIntentTemplate(R.id.stack_view, toastPendingIntent)
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}