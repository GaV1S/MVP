package ru.s1aks.mvp_login_activity.ui.login

import ru.s1aks.mvp_login_activity.data.db.UserEntity
import ru.s1aks.mvp_login_activity.ui.utils.Publisher

interface LoginActivityContract {

    interface LoginViewModel {

        val showProgress : Publisher<Boolean>
        val isLoginSuccess : Publisher<String>
        val isLogout : Publisher<Boolean>
        val receivedUser : Publisher<UserEntity>
        val receivedUserList : Publisher<String>
        val messenger : Publisher<String>

        fun onCheckOnAppStartAuthorization()
        fun onDeleteUser(login: String)
        fun onGetUser(login: String)
        fun onGetUserList()
        fun onLogin(login: String, password: String)
        fun onLogout()
        fun onPasswordRemind(login: String)
        fun onUpdateUser(userId: Int, login: String, password: String)
    }
}