package cloudeleven.space.a201811test_auth01

import android.app.Application
import android.content.Context

class App : Application() {
    init {
        instance = this
    }

    companion object {
        private var instance: App? = null

        fun applicationContext() : Context {
            return instance!!.applicationContext
        }

        fun getToken() : String {
            val pref = applicationContext().getSharedPreferences("auth", Context.MODE_PRIVATE)
            return pref.getString("token", "")
        }
    }
/*
    override fun onCreate() {
        super.onCreate()
        // initialize for any

        // Use ApplicationContext.
        // example: SharedPreferences etc...
        val context: Context = App.applicationContext()
    }
    */
}