package cloudeleven.space.a201811test_auth01.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import cloudeleven.space.a201811test_auth01.models.UserInfoModel
import io.reactivex.observers.DisposableSingleObserver
import retrofit2.HttpException

class UserInfoViewModel(val model: UserInfoModel) {
    private val userInfoObservable = MutableLiveData<UserInfoModel.UserInfoEntity>()
    private val userInfoErrorObservable = MutableLiveData<HttpException>()
    private var schedulersWrapper = SchedulersWrapper()

    fun getUserInfoObservable(): LiveData<UserInfoModel.UserInfoEntity> = userInfoObservable
    fun getUserInfoErrorObservable(): LiveData<HttpException> = userInfoErrorObservable

    fun getUserInfo() {
        model.getAuthenticatedUser()!!.subscribeOn(schedulersWrapper.io()).observeOn(
            schedulersWrapper.main()).subscribeWith(object : DisposableSingleObserver<UserInfoModel.UserInfoEntity?>() {
            override fun onSuccess(t: UserInfoModel.UserInfoEntity) {
                userInfoObservable.postValue(t)
            }

            override fun onError(e: Throwable) {
                userInfoErrorObservable.postValue(e as HttpException)
            }
        })
    }
}