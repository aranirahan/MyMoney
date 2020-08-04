package com.aranirahan.mymoney

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.widget.RemoteViews
import android.widget.Toast


class BroadcastWidget : AppWidgetProvider() {
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

        if (ACTION_ADD == intent.action) {
            val value = initValue(context, 1)
            updateWidget(context, value)
            Toast.makeText(context, "Add 1", Toast.LENGTH_SHORT).show()
        } else if (ACTION_MIN == intent.action) {
            val value = initValue(context, -5)
            updateWidget(context, value)
            Toast.makeText(context, "Min 5", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initValue(context: Context, addingValue:Int) : Int{
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        var value = sharedPreferences.getInt(VALUE, 0)
        value += addingValue

        val editor = sharedPreferences.edit()
        editor.putInt(VALUE, value)
        editor.apply()

        return value
    }

    private fun updateWidget(context: Context, value: Int) {
        val views = RemoteViews(context.packageName, R.layout.broadcast_widget)
        views.setTextViewText(R.id.tvWidget, value.toString())

        val appWidget = ComponentName(context, BroadcastWidget::class.java)
        val appWidgetManager = AppWidgetManager.getInstance(context)
        appWidgetManager.updateAppWidget(appWidget, views)

    }

    companion object {
        private const val ACTION_ADD = "ACTION_ADD"
        private const val ACTION_MIN = "ACTION_MIN"
        private const val VALUE = "VALUE"
        private const val PREF_NAME = "PREF_NAME"

        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val views = RemoteViews(context.packageName, R.layout.broadcast_widget)
            views.setOnClickPendingIntent(R.id.btn_add, getPendingSelfIntent(context, ACTION_ADD))
            views.setOnClickPendingIntent(R.id.btn_min, getPendingSelfIntent(context, ACTION_MIN))
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        private fun getPendingSelfIntent(context: Context?, action: String): PendingIntent? {
            val intent = Intent(context, BroadcastWidget::class.java)
            intent.action = action
            return PendingIntent.getBroadcast(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        }
    }
}