package cloudeleven.space.a201811test_auth01

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import cloudeleven.space.a201811test_auth01.viewmodel.LoginViewModel
import cloudeleven.space.a201811test_auth01.models.LoginModel

class LoginFragment : Fragment(), LoginWebViewClient.OnCodeRetrievedListener {
    private lateinit var loginViewModel: LoginViewModel

    private var onTokenRetrievedListener: OnTokenRetrievedListener? = null

    interface OnTokenRetrievedListener {
        fun onTokenRetrieved(token: String)
    }
    fun setOnTokenRetrievedListener(listener: OnTokenRetrievedListener) {
        this.onTokenRetrievedListener = listener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginViewModel = LoginViewModel(LoginModel())
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        listenToObservables()
        val view = inflater.inflate(R.layout.login_fragment, container, false)
        loadLoginPage(view)
        return view
    }
    private fun listenToObservables() {
        loginViewModel.getTokenObservable().observe(this, Observer {
//            hideProgressBar()
            onTokenRetrievedListener?.onTokenRetrieved(it!!)
        })
        loginViewModel.getTokenErrorObservable().observe(this, Observer {
//            hideProgressBar()
            showErrorMessage(it!!.message())
        })
    }

    private fun loadLoginPage(view: View) {
        val clientId = resources.getString(R.string.client_id)
        val scope = resources.getString(R.string.scope)
        val state = resources.getString(R.string.state)
        val uri = "https://qiita.com/api/v2/oauth/authorize?" +
                "client_id=" + clientId +
                "&scope=" + scope +
                "&state=" + state

        val client = LoginWebViewClient()
        client.setOnCodeRetrievedListener(this)
        view.findViewById<WebView>(R.id.webview).apply {
            webViewClient = client
            settings.javaScriptEnabled = true
            loadUrl(uri)
        }
    }

    override fun onCodeRetrieved(code: String) {
        loginViewModel.retrieveTokenByCode(code)
    }

    fun showErrorMessage(errorMsg: String) {
        Toast.makeText(App.applicationContext(), "Error retrieving data: $errorMsg", Toast.LENGTH_SHORT).show()
    }
}

class LoginWebViewClient : WebViewClient() {
    private var onCodeRetrievedListener: OnCodeRetrievedListener? = null
    interface OnCodeRetrievedListener {
        fun onCodeRetrieved(code: String)
    }
    fun setOnCodeRetrievedListener(listener: OnCodeRetrievedListener) {
        this.onCodeRetrievedListener = listener
    }

    override fun doUpdateVisitedHistory(view: WebView?, url: String?, isReload: Boolean) {
        if (url != null && !url.isEmpty()) {
            android.util.Log.d("xtc", String.format("%s : %s", "load", url))
            val redirectUri = App.applicationContext().resources.getString(R.string.redirect_uri)
            if (url.startsWith(redirectUri)) {
                val code = android.net.Uri.parse(url).getQueryParameter("code")
                android.util.Log.d("xtc", String.format("code = %s", code))
                code?.let {
                    onCodeRetrievedListener?.onCodeRetrieved(it)
                }
                return
            }
        }
        super.doUpdateVisitedHistory(view, url, isReload)
    }
}


