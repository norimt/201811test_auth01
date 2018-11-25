package cloudeleven.space.a201811test_auth01

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import cloudeleven.space.a201811test_auth01.viewmodel.UserInfoViewModel
import cloudeleven.space.a201811test_auth01.models.UserInfoModel
import kotlinx.android.synthetic.main.fragment_user_info.*


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

    private lateinit var userInfoViewModel: UserInfoViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userInfoViewModel = UserInfoViewModel(UserInfoModel())
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        listenToObservables()
        val view = inflater.inflate(R.layout.fragment_user_info, container, false)
        userInfoViewModel.getUserInfo()
        return view
    }
    private fun listenToObservables() {
        userInfoViewModel.getUserInfoObservable().observe(this, Observer {
            //            hideProgressBar()
            showUserInfo(it!!)
        })
        userInfoViewModel.getUserInfoErrorObservable().observe(this, Observer {
            //            hideProgressBar()
            showErrorMessage(it!!.message())
        })
    }

    fun showUserInfo(info: UserInfoModel.UserInfoEntity) {
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

    fun showErrorMessage(errorMsg: String) {
        Toast.makeText(App.applicationContext(), "Error retrieving data: $errorMsg", Toast.LENGTH_SHORT).show()
    }
}
