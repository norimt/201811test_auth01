package cloudeleven.space.a201811test_auth01

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

class LoginFragment : Fragment(), LoginWebViewClient.OnCodeRetrievedListener {
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private var onTokenRetrievedListener: OnTokenRetrievedListener? = null

    interface OnTokenRetrievedListener {
        fun onTokenRetrieved(code: String)
    }
    fun setOnTokenRetrievedListener(listener: OnTokenRetrievedListener) {
        this.onTokenRetrievedListener = listener
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.login_fragment, container, false)
        loadLoginPage(view)
        return view
    }
    override fun onStop() {
        super.onStop()
        compositeDisposable.clear()
    }

    fun loadLoginPage(view: View) {
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
        requestToken(code)
    }


    private fun requestToken(code: String) {
        val disposable: Disposable = requestTokenByCode(code)!!.subscribeOn(Schedulers.io()).observeOn(
            AndroidSchedulers.mainThread()).subscribeWith(object : DisposableObserver<AccessTokenEntity?>() {
            override fun onNext(t: AccessTokenEntity) {
//                    hideProgressBar()
                onTokenRetrievedListener?.onTokenRetrieved(t.token)
            }

            override fun onStart() {
//                    showProgressBar()
            }

            override fun onComplete() {
            }

            override fun onError(e: Throwable) {
//                    main_activity_progress_bar.visibility = View.GONE
//                    Toast.makeText(this@MainActivity, "Error retrieving data: ${e.message}", Toast.LENGTH_SHORT)
            }
        })
        compositeDisposable.add(disposable)
    }
    private var mRetrofit: Retrofit? = null

    private fun requestTokenByCode(code: String): Observable<AccessTokenEntity>? {
        if (mRetrofit == null) {
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            val client = OkHttpClient.Builder().addInterceptor(loggingInterceptor).build()
            mRetrofit = Retrofit.Builder().baseUrl("https://qiita.com/").addConverterFactory(
                GsonConverterFactory.create()).addCallAdapterFactory(RxJava2CallAdapterFactory.create()).client(client).build()
        }
        val clientId = App.applicationContext().resources.getString(R.string.client_id)
        val clientSecret = App.applicationContext().resources.getString(R.string.client_secret)
        val request = AccessTokenRequestModel(clientId, clientSecret, code)
        return mRetrofit?.create(AuthService::class.java)?.accessTokens(request)
    }

    interface AuthService {
        @POST("api/v2/access_tokens")
        fun accessTokens(@Body accessTokenRequestModel: AccessTokenRequestModel): Observable<AccessTokenEntity>
    }

    data class AccessTokenRequestModel(var client_id: String,
                                       var client_secret: String,
                                       var code: String)
    data class AccessTokenEntity(val client_id: String,
                                 val token: String)


}

class LoginWebViewClient : WebViewClient() {
    private var onCodeRetrievedListener: OnCodeRetrievedListener? = null
    interface OnCodeRetrievedListener {
        fun onCodeRetrieved(code: String)
    }
    fun setOnCodeRetrievedListener(listener: OnCodeRetrievedListener) {
        this.onCodeRetrievedListener = listener
    }
    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        if (view != null && view is WebView) {
            val uri = view.url
            val uriString = uri.toString()
            android.util.Log.d("xtc", String.format("%s : %s", "load", uriString))
            val redirectUri = App.applicationContext().resources.getString(R.string.redirect_uri)
            if (uriString.startsWith(redirectUri)) {
                val code = android.net.Uri.parse(uriString).getQueryParameter("code")
                android.util.Log.d("xtc", String.format("code = %s", code))
                onCodeRetrievedListener?.onCodeRetrieved(code)
                return true
            }
        }
        android.util.Log.d("xtc", "view is null or not webview or not redirect")
        return super.shouldOverrideUrlLoading(view, request)
    }

    override fun doUpdateVisitedHistory(view: WebView?, url: String?, isReload: Boolean) {
        if (url != null && !url.isEmpty()) {
            android.util.Log.d("xtc", String.format("%s : %s", "load", url))
            val redirectUri = App.applicationContext().resources.getString(R.string.redirect_uri)
            if (url.startsWith(redirectUri)) {
                val code = android.net.Uri.parse(url).getQueryParameter("code")
                android.util.Log.d("xtc", String.format("code = %s", code))
                onCodeRetrievedListener?.onCodeRetrieved(code)
                return
            }
        }
        super.doUpdateVisitedHistory(view, url, isReload)
    }
}


