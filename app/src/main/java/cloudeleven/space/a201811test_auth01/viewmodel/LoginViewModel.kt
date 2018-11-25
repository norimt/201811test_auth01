package cloudeleven.space.a201811test_auth01.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import cloudeleven.space.a201811test_auth01.models.LoginModel
import io.reactivex.observers.DisposableSingleObserver
import retrofit2.HttpException

class LoginViewModel(var model: LoginModel) {
    private val tokenObservable = MutableLiveData<String>()
    private val tokenErrorObservable = MutableLiveData<HttpException>()
    private var schedulersWrapper = SchedulersWrapper()

    fun getTokenObservable(): LiveData<String> = tokenObservable
    fun getTokenErrorObservable(): LiveData<HttpException> = tokenErrorObservable

    fun retrieveTokenByCode(code: String) {
        model.requestTokenByCode(code)!!.subscribeOn(schedulersWrapper.io()).observeOn(
            schedulersWrapper.main()).subscribeWith(object : DisposableSingleObserver<LoginModel.AccessTokenEntity?>() {

            override fun onSuccess(t: LoginModel.AccessTokenEntity) {
                tokenObservable.postValue(t.token)
            }

            override fun onError(e: Throwable) {
                tokenErrorObservable.postValue(e as HttpException)
            }
        })
    }

}