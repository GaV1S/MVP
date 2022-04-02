package ru.s1aks.mvp_login_activity.ui

import ru.s1aks.mvp_login_activity.domain.LoginData

class Contract {
    enum class ViewState {
        IDLE, LOADING, SUCCESS, ERROR
    }

    interface View {
        fun setState(state: ViewState)
        fun setLoginError(errorCode: Int)
        fun setLoginData(loginData: LoginData)
        fun setLoginAllow(loginAllowState: Boolean)
        fun setMessage(message: String)
    }

    interface Presenter {
        fun onAttach(view: View)
        fun onChangeLogin(login: String)
        fun onLoginRequest(loginData: LoginData)
        fun onRegistration(loginData: LoginData)
        fun onForgotPassword(loginData: LoginData)
        fun onDetach()
    }
}