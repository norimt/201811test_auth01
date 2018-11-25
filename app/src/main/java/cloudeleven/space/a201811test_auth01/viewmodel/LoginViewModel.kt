package cloudeleven.space.a201811test_auth01.viewmodel

import cloudeleven.space.a201811test_auth01.models.LoginModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.subjects.PublishSubject
import retrofit2.HttpException

class LoginViewModel() {
    lateinit var tokenObservable: PublishSubject<String>
    lateinit var tokenErrorObservable: PublishSubject<HttpException>
    private lateinit var model: LoginModel
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private var schedulersWrapper = SchedulersWrapper()

    constructor(loginModel: LoginModel) : this() {
        this.model = loginModel
        tokenObservable = PublishSubject.create()
        tokenErrorObservable = PublishSubject.create()
    }

    fun retrieveTokenByCode(code: String) {
        val disposable: Disposable = model.requestTokenByCode(code)!!.subscribeOn(schedulersWrapper.io()).observeOn(
            schedulersWrapper.main()).subscribeWith(object : DisposableSingleObserver<LoginModel.AccessTokenEntity?>() {

            override fun onSuccess(t: LoginModel.AccessTokenEntity) {
                tokenObservable.onNext(t.token)
            }

            override fun onError(e: Throwable) {
                tokenErrorObservable.onNext(e as HttpException)
            }
        })
        compositeDisposable.add(disposable)
    }

    fun cancelNetworkConnections() {
        compositeDisposable.clear()
    }
}