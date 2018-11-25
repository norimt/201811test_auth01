package cloudeleven.space.a201811test_auth01.models

import cloudeleven.space.a201811test_auth01.App
import cloudeleven.space.a201811test_auth01.R
import io.reactivex.Single
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

class LoginModel() {
    private var retrofit: Retrofit? = null

    fun requestTokenByCode(code: String): Single<AccessTokenEntity>? {
        val clientId = App.applicationContext().resources.getString(R.string.client_id)
        val clientSecret = App.applicationContext().resources.getString(R.string.client_secret)
        val request = AccessTokenRequestModel(clientId, clientSecret, code)
        return getRetrofit()?.create(AuthService::class.java)?.accessTokens(request)
    }

    private fun getRetrofit(): Retrofit? {
        if (retrofit == null) {
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            val client = OkHttpClient.Builder().addInterceptor(loggingInterceptor).build()
            retrofit = Retrofit.Builder().baseUrl("https://qiita.com/").addConverterFactory(
                GsonConverterFactory.create()).addCallAdapterFactory(RxJava2CallAdapterFactory.create()).client(client).build()
        }
        return retrofit
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