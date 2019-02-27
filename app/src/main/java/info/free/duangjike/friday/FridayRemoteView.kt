package info.free.duangjike.friday

import android.widget.RemoteViews
import info.free.duangjike.R
import info.free.duangjike.Util.today
import java.util.*
import java.util.Calendar.DAY_OF_WEEK

/**
 * Created by zhufree on 2018/12/13.
 *
 */

class FridayRemoteView(packageName: String) : RemoteViews(packageName, R.layout.layout_widget) {


    fun updateDayText() {
        setTextViewText(R.id.tv_widget_date, "${getWeekDayString(today.get(DAY_OF_WEEK))} " +
                "${today.get(Calendar.YEAR)}.${today.get(Calendar.MONTH) + 1}." +
                "${today.get(Calendar.DAY_OF_MONTH)}")
        val yesString = if (FridayPreference.getLang() == 0) "是" else "YES!"
        val noString = if (FridayPreference.getLang() == 0) "不是" else "NO"
        setTextViewText(R.id.tv_widget_is_friday, if (today.get(DAY_OF_WEEK) == 6) yesString else noString)
    }

    private fun getWeekDayString(day: Int): String {
        return when (day) {
            1 -> "Sunday"
            2 -> "Monday"
            3 -> "Tuesday"
            4 -> "Wednesday"
            5 -> "Thursday"
            6 -> "Friday"
            7 -> "Saturday"
            else -> "Friday"
        }
    }
}
