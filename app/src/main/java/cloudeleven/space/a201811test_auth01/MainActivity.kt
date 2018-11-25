package cloudeleven.space.a201811test_auth01

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle


class MainActivity : AppCompatActivity(), LoginFragment.OnTokenRetrievedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val token = App.getToken()
        if (token.isEmpty()) {
            if (supportFragmentManager.findFragmentByTag("loginFragment") == null) {
                val fragment = LoginFragment()
                fragment.setOnTokenRetrievedListener(this)
                supportFragmentManager.beginTransaction()
                    .add(R.id.container, fragment, "loginFragment")
                    .commit()
            }
        } else {
            showUserInfoFragment()
        }
    }
    override fun onTokenRetrieved(token: String) {
        android.util.Log.d("xtc", "Activity onTokenRetrieved called")
        val pref = getSharedPreferences("auth", Context.MODE_PRIVATE)
        pref.edit().putString("token", token).apply()
        showUserInfoFragment()
    }

    private fun showUserInfoFragment() {
        val fragment = UserInfoFragment()
//        val bundle = Bundle()
//        bundle.putString("token", token)
//        fragment.arguments = bundle
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .commit()
    }

}
