package cloudeleven.space.a201811test_auth01.controllers

import cloudeleven.space.a201811test_auth01.LoginFragment
import cloudeleven.space.a201811test_auth01.models.LoginModel

class LoginController {
    private lateinit var view: LoginFragment
    private lateinit var model: LoginModel

    infix fun hasView(loginFragment: LoginFragment) {
        this.view = loginFragment
    }

    infix fun hasModel(loginModel: LoginModel) {
        this.model = loginModel
    }

    fun requestTokenByCode(code: String) {
//        view.showProgressBar()
        model.retrieveTokenByCode(code)
    }

    fun onStop() {
        model.stopRetrievingToken()
    }

    fun doWhenAccessTokenReady() {
//        view.hideProgressBar()
        view.doWhenAccessTokenReady()
    }

    fun doWhenThereIsErrorFetchingToken() {
//        view.hideProgressBar()
        view.showError()
    }

}