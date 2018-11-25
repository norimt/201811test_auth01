package cloudeleven.space.a201811test_auth01.controllers

import cloudeleven.space.a201811test_auth01.UserInfoFragment
import cloudeleven.space.a201811test_auth01.models.UserInfoModel

class UserInfoController {
    private lateinit var view: UserInfoFragment
    private lateinit var model: UserInfoModel

    infix fun hasView(userInfoFragment: UserInfoFragment) {
        this.view = userInfoFragment
    }

    infix fun hasModel(userInfoModel: UserInfoModel) {
        this.model = userInfoModel
    }

    fun getUserInfo() {
//        view.showProgressBar()
        model.getUserInfo()
    }

    fun onStop() {
        model.stopGettingUserInfo()
    }

    fun doWhenUserInfoReady() {
//        view.hideProgressBar()
        view.showUserInfo()
    }

    fun doWhenThereIsErrorFetchingToken() {
//        view.hideProgressBar()
        view.showError()
    }

}