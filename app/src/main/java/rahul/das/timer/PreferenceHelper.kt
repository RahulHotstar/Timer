package rahul.das.timer

import android.content.Context

class PreferenceHelper {

    public fun saveMaxTime(minutes:Int, context: Context){
        var current = System.currentTimeMillis();
        current += (minutes * 60 * 1000L)
        context.getSharedPreferences("timer", Context.MODE_PRIVATE).edit()
            .putLong("max", current)
            .apply()
    }

    public fun getMaxTime(context: Context):Long{
        return context.getSharedPreferences("timer", Context.MODE_PRIVATE)
            .getLong("max", 0)
    }



}