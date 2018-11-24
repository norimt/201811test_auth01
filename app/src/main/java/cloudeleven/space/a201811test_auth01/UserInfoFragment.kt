package cloudeleven.space.a201811test_auth01

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_user_info.*
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header


class UserInfoFragment : Fragment() {
    object Constants {
        const val ID = "id"
        const val PERM_ID = "permanent_id"
        const val NAME = "name"
        const val DESC = "description"
        const val FOLLOWEES = "followees_count"
        const val FOLLOWERS = "followers_count"
        const val LOCATION = "location"
        const val ORG = "organization"
        const val GITHUB = "github_login_name"
        const val WEBSITE = "website_url"
    }


    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_user_info, container, false)
        getUserInfoAndShow()
        return view
    }

    private fun showUserInfo(info: UserInfoEntity) {
        android.util.Log.d("xtc", String.format("userinfo = %s", info.toString()))
        qiita_id.text = "${UserInfoFragment.Constants.ID.capitalize()}: ${info.id}"
        perm_id.text = "${UserInfoFragment.Constants.PERM_ID.capitalize()}: ${info.permanent_id}"
        name.text = "${UserInfoFragment.Constants.NAME.capitalize()}: ${info.name}"
        desc.text = "${UserInfoFragment.Constants.DESC.capitalize()}: ${info.description}"
        followees.text = "${UserInfoFragment.Constants.FOLLOWEES.capitalize()}: ${info.followees_count}"
        followers.text = "${UserInfoFragment.Constants.FOLLOWERS.capitalize()}: ${info.followers_count}"
        location.text = "${UserInfoFragment.Constants.LOCATION.capitalize()}: ${info.location}"
        org.text = "${UserInfoFragment.Constants.ORG.capitalize()}: ${info.organization}"
        github.text = "${UserInfoFragment.Constants.GITHUB.capitalize()}: ${info.github_login_name}"
        website.text = "${UserInfoFragment.Constants.WEBSITE.capitalize()}: ${info.website_url}"
    }


    private fun getUserInfoAndShow() {
        val disposable: Disposable = getAuthenticatedUser()!!.subscribeOn(Schedulers.io()).observeOn(
            AndroidSchedulers.mainThread()).subscribeWith(object : DisposableObserver<UserInfoEntity?>() {
            override fun onNext(t: UserInfoEntity) {
//                    hideProgressBar()
                showUserInfo(t)
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
    private fun getAuthenticatedUser(): Observable<UserInfoEntity>? {
        if (mRetrofit == null) {
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            val client = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .addInterceptor(BearerAuthenticationInterceptor())
                .build()
            mRetrofit = Retrofit.Builder().baseUrl("https://qiita.com/").addConverterFactory(
                GsonConverterFactory.create()).addCallAdapterFactory(RxJava2CallAdapterFactory.create()).client(client).build()
        }
        val clientId = App.applicationContext().resources.getString(R.string.client_id)
        val clientSecret = App.applicationContext().resources.getString(R.string.client_secret)
        return mRetrofit?.create(UserInfoService::class.java)?.authenticatedUser()
    }

    interface UserInfoService {
        @GET("api/v2/authenticated_user")
        fun authenticatedUser(): Observable<UserInfoEntity>
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
