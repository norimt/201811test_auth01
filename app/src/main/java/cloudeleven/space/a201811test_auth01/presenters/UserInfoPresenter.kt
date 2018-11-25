package cloudeleven.space.a201811test_auth01.presenters

import cloudeleven.space.a201811test_auth01.UserInfoFragment
import cloudeleven.space.a201811test_auth01.models.UserInfoModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableSingleObserver

class UserInfoPresenter() {
    private lateinit var view: UserInfoFragment
    private lateinit var model: UserInfoModel
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private var schedulersWrapper = SchedulersWrapper()

    infix fun hasView(userInfoFragment: UserInfoFragment) {
        this.view = userInfoFragment
    }

    constructor(userInfoModel: UserInfoModel) : this() {
        this.model = userInfoModel
    }

    fun getUserInfo() {
        val disposable: Disposable = model.getAuthenticatedUser()!!.subscribeOn(schedulersWrapper.io()).observeOn(
            schedulersWrapper.main()).subscribeWith(object : DisposableSingleObserver<UserInfoModel.UserInfoEntity?>() {
            override fun onSuccess(t: UserInfoModel.UserInfoEntity) {
//                    hideProgressBar()
                view.showUserInfo(t)
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