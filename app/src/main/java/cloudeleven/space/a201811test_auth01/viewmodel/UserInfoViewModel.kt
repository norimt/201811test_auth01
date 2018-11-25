package cloudeleven.space.a201811test_auth01.viewmodel

import cloudeleven.space.a201811test_auth01.models.UserInfoModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.subjects.PublishSubject
import retrofit2.HttpException

class UserInfoViewModel() {
    lateinit var userInfoObservable: PublishSubject<UserInfoModel.UserInfoEntity>
    lateinit var userInfoErrorObservable: PublishSubject<HttpException>
    private lateinit var model: UserInfoModel
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private var schedulersWrapper = SchedulersWrapper()

    constructor(userInfoModel: UserInfoModel) : this() {
        this.model = userInfoModel
        userInfoObservable = PublishSubject.create()
        userInfoErrorObservable = PublishSubject.create()
    }

    fun getUserInfo() {
        val disposable: Disposable = model.getAuthenticatedUser()!!.subscribeOn(schedulersWrapper.io()).observeOn(
            schedulersWrapper.main()).subscribeWith(object : DisposableSingleObserver<UserInfoModel.UserInfoEntity?>() {
            override fun onSuccess(t: UserInfoModel.UserInfoEntity) {
                userInfoObservable.onNext(t)
            }

            override fun onError(e: Throwable) {
                userInfoErrorObservable.onNext(e as HttpException)
            }
        })
        compositeDisposable.add(disposable)
    }

    fun cancelNetworkConnections() {
        compositeDisposable.clear()
    }

}