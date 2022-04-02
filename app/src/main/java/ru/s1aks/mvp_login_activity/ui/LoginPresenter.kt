package ru.s1aks.mvp_login_activity.ui

import android.os.Handler
import android.os.Looper
import ru.s1aks.mvp_login_activity.domain.LoginData

private const val MOCK_LOADING_DURATION = 3000L
private const val SHORT_LOGIN_ERROR = 0
private const val INVALID_SYMBOLS_IN_LOGIN_ERROR = 1
private const val LOGIN_NOT_EXIST_ERROR = 2
private const val REGISTRATION_MESSAGE = "Вы будете перемещены на страницу регистрации."
private const val FORGOT_PASSWORD_MESSAGE =
    "На почту, указанную при регистрации выслан новый пароль для входа."

class LoginPresenter : Contract.Presenter {
    private var view: Contract.View? = null

    override fun onAttach(view: Contract.View) {
        this.view = view
        view.setState(Contract.ViewState.IDLE)
    }

    override fun onLoginRequest(loginData: LoginData) {
        view?.setState(Contract.ViewState.LOADING)
        Handler(Looper.getMainLooper()).postDelayed({
            view?.setState(Contract.ViewState.SUCCESS)
        }, MOCK_LOADING_DURATION)
    }

    override fun onRegistration(loginData: LoginData) {
        view?.setState(Contract.ViewState.LOADING)
        Handler(Looper.getMainLooper()).postDelayed({
            view?.setState(Contract.ViewState.SUCCESS)
        }, MOCK_LOADING_DURATION)
        view?.setMessage(REGISTRATION_MESSAGE)
    }

    override fun onForgotPassword(loginData: LoginData) {
        view?.setState(Contract.ViewState.LOADING)
        Handler(Looper.getMainLooper()).postDelayed({
            view?.setState(Contract.ViewState.SUCCESS)
        }, MOCK_LOADING_DURATION)
        view?.setMessage(FORGOT_PASSWORD_MESSAGE)
    }

    override fun onChangeLogin(login: String) {
        when {
            login.length < 3 -> view?.setLoginError(SHORT_LOGIN_ERROR)
            !login.matches("[^\\W]+".toRegex()) -> view?.setLoginError(
                INVALID_SYMBOLS_IN_LOGIN_ERROR)
            login !in listOf("admin", "user01", "guest") -> view?.setLoginError(
                LOGIN_NOT_EXIST_ERROR)
            else -> view?.setLoginAllow(true)
        }
    }

    override fun onDetach() {
        view = null
    }

}