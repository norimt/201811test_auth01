package cloudeleven.space.a201811test_auth01.presenters

import cloudeleven.space.a201811test_auth01.LoginFragment
import cloudeleven.space.a201811test_auth01.models.LoginModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableSingleObserver

class LoginPresenter() {
    private lateinit var view: LoginFragment
    private lateinit var model: LoginModel
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private var schedulersWrapper = SchedulersWrapper()

    infix fun hasView(loginFragment: LoginFragment) {
        this.view = loginFragment
    }

    constructor(loginModel: LoginModel) : this() {
        this.model = loginModel
    }

    fun retrieveTokenByCode(code: String) {
        val disposable: Disposable = model.requestTokenByCode(code)!!.subscribeOn(schedulersWrapper.io()).observeOn(
            schedulersWrapper.main()).subscribeWith(object : DisposableSingleObserver<LoginModel.AccessTokenEntity?>() {

            override fun onSuccess(t: LoginModel.AccessTokenEntity) {
                view.doWhenAccessTokenReady(t.token)
            }

            override fun onError(e: Throwable) {
                view.showErrorMessage(e.message!!)
            }
        })
        compositeDisposable.add(disposable)
    }

    fun onStop() {
        compositeDisposable.clear()
    }
}