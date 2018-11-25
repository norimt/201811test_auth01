package cloudeleven.space.a201811test_auth01.models

import cloudeleven.space.a201811test_auth01.App
import io.reactivex.Single
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

class UserInfoModel() {
    private var retrofit: Retrofit? = null

    class BearerAuthenticationInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain?): Response? {
            chain ?: return null

            val accessToken = App.getToken()
            val request = chain.request().newBuilder()
                .header("Authorization", "Bearer ${accessToken}")
                .build()
            return chain.proceed(request)
        }
    }
    fun getAuthenticatedUser(): Single<UserInfoEntity>? {
        return getRetrofit()?.create(UserInfoService::class.java)?.authenticatedUser()
    }

    private fun getRetrofit(): Retrofit? {
        if (retrofit == null) {
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            val client = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .addInterceptor(BearerAuthenticationInterceptor())
                .build()
            retrofit = Retrofit.Builder().baseUrl("https://qiita.com/").addConverterFactory(
                GsonConverterFactory.create()).addCallAdapterFactory(RxJava2CallAdapterFactory.create()).client(client).build()
        }
        return retrofit
    }

    interface UserInfoService {
        @GET("api/v2/authenticated_user")
        fun authenticatedUser(): Single<UserInfoEntity>
    }

    data class UserInfoEntity(val description: String,
                              val followees_count: Int,
                              val followers_count: Int,
                              val id: String,
                              val name: String,
                              val location: String,
                              val organization: String,
                              val permanent_id: Int,
                              val website_url: String,
                              val github_login_name: String)

}