package ru.s1aks.mvp_login_activity.ui

import moxy.MvpPresenter
import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle
import ru.s1aks.mvp_login_activity.domain.LoginData

class Contract {
    enum class ViewState {
        IDLE, LOADING, SUCCESS, ERROR
    }

    interface View : MvpView {

        @AddToEndSingle
        fun setState(state: ViewState)

        @AddToEndSingle
        fun setLoginError(errorCode: Int)

        @AddToEndSingle
        fun setLoginData(loginData: LoginData)

        @AddToEndSingle
        fun setLoginAllow(loginAllowState: Boolean)

        @AddToEndSingle
        fun setMessage(message: String)
    }

    abstract class Presenter : MvpPresenter<View>() {
        abstract fun onChangeLogin(login: String)
        abstract fun onLoginRequest(loginData: LoginData)
        abstract fun onRegistration(loginData: LoginData)
        abstract fun onForgotPassword(loginData: LoginData)
    }
}