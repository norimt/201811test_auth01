package cloudeleven.space.a201811test_auth01

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import cloudeleven.space.a201811test_auth01.controllers.LoginController
import cloudeleven.space.a201811test_auth01.models.LoginModel

class LoginFragment : Fragment(), LoginWebViewClient.OnCodeRetrievedListener {
    private lateinit var loginController: LoginController
    private lateinit var loginModel: LoginModel

    private var onTokenRetrievedListener: OnTokenRetrievedListener? = null

    interface OnTokenRetrievedListener {
        fun onTokenRetrieved(token: String)
    }
    fun setOnTokenRetrievedListener(listener: OnTokenRetrievedListener) {
        this.onTokenRetrievedListener = listener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginController = LoginController()
        loginModel = LoginModel(loginController)
        loginController hasView this
        loginController hasModel loginModel
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.login_fragment, container, false)
        loadLoginPage(view)
        return view
    }
    override fun onStop() {
        super.onStop()
        loginController.onStop()
    }

    fun doWhenAccessTokenReady() {
        android.util.Log.d("xtc", String.format("Fragment doWhenAccessTokenReady called with %s", loginModel.tokenEntity.token))
        onTokenRetrievedListener?.onTokenRetrieved(loginModel.tokenEntity.token)
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
        val webview = view.findViewById<WebView>(R.id.webview).apply {
            webViewClient = client
            settings.javaScriptEnabled = true
            loadUrl(uri)
        }
    }

    override fun onCodeRetrieved(code: String) {
        loginController.requestTokenByCode(code)
    }

    fun showError() {
        showToast(App.applicationContext(), getString(R.string.error_getting_results, loginModel.httpException.message))
    }

    private fun showToast(context: Context, msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
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


