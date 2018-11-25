package cloudeleven.space.a201811test_auth01.models

import cloudeleven.space.a201811test_auth01.App
import cloudeleven.space.a201811test_auth01.R
import cloudeleven.space.a201811test_auth01.SchedulersWrapper
import cloudeleven.space.a201811test_auth01.controllers.LoginController
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableSingleObserver
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

class LoginModel(val controller: LoginController) {
    private var retrofit: Retrofit? = null
    var tokenEntity = AccessTokenEntity("","")
    lateinit var httpException: HttpException
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private var schedulersWrapper = SchedulersWrapper()

    fun retrieveTokenByCode(code: String) {
        val disposable: Disposable = requestTokenByCode(code)!!.subscribeOn(schedulersWrapper.io()).observeOn(
            schedulersWrapper.main()).subscribeWith(object : DisposableSingleObserver<AccessTokenEntity?>() {

            override fun onSuccess(t: AccessTokenEntity) {
                tokenEntity = t
                controller.doWhenAccessTokenReady()
            }

            override fun onError(e: Throwable) {
                httpException = e as HttpException
                controller.doWhenThereIsErrorFetchingToken()
            }
        })
        compositeDisposable.add(disposable)
    }

    fun stopRetrievingToken() {
        compositeDisposable.clear()
    }

    private fun requestTokenByCode(code: String): Single<AccessTokenEntity>? {
        if (retrofit == null) {
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            val client = OkHttpClient.Builder().addInterceptor(loggingInterceptor).build()
            retrofit = Retrofit.Builder().baseUrl("https://qiita.com/").addConverterFactory(
                GsonConverterFactory.create()).addCallAdapterFactory(RxJava2CallAdapterFactory.create()).client(client).build()
        }
        val clientId = App.applicationContext().resources.getString(R.string.client_id)
        val clientSecret = App.applicationContext().resources.getString(R.string.client_secret)
        val request = AccessTokenRequestModel(clientId, clientSecret, code)
        return retrofit?.create(AuthService::class.java)?.accessTokens(request)
    }

    interface AuthService {
        @POST("api/v2/access_tokens")
        fun accessTokens(@Body accessTokenRequestModel: AccessTokenRequestModel): Single<AccessTokenEntity>
    }

    data class AccessTokenRequestModel(var client_id: String,
                                       var client_secret: String,
                                       var code: String)
    data class AccessTokenEntity(val client_id: String,
                                 val token: String)

}