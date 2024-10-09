package js.apps.snakegame

import android.content.Context
import android.content.SharedPreferences

class RecordSP(context: Context)  {
    companion object {
        const val RECORD_KEY = "record"
    }

    private val c = context.getSharedPreferences("user_record", Context.MODE_PRIVATE)
    fun getRecord(): Int {
        return c.getInt(RECORD_KEY, 0)

    }

    fun setRecord(record: Int) {
        c.edit().putInt(RECORD_KEY, record).apply()
    }

}