package com.yj.addwords

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews


class NewAppWidget : AppWidgetProvider() {
    /*
    위젯 갱신 주기에 따라 위젯을 갱신할때 호출
    위젯 xml에서 설정한 updatePeriodMillis에 따라 주기적으로 호출되는 메소드이며 초기 위젯 생성시의 initialize 작업도 수행, 보통 handler를 넣어준다
    * onUpdate 구현시 주의사항: 다수의 widget에 대한 업데이트 관리를 위해 마지막 파라미터인 appWidgetIds의 개수 만큼 돌면서 위젯의 업데이트를 구현해야 한다
    */
    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }
    //    위젯이 처음 생성될때 호출되며, 동일한 위젯의 경우 첫번째만 호출 (주로 DB 설정 함)
    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }
    //    위젯의 마지막 인스턴스가 제거될때 호출 (onEnabled에서 설정한 작업들은 해제하는 작업들을 진행합니다. )
    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}
//  위젯에 반영
internal fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
    val widgetText = context.getString(R.string.appwidget_text)
    // Construct the RemoteViews object
    val views = RemoteViews(context.packageName, R.layout.new_app_widget)
    views.setTextViewText(R.id.appwidget_word, widgetText)

    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views)
}

//    위젯이 사용자에 의해 제거될때 호출 - onDeleted
