package ru.s1aks.mvp_login_activity.ui

import android.os.Handler
import android.os.Looper
import com.github.terrakok.cicerone.Router
import ru.s1aks.mvp_login_activity.Screens
import ru.s1aks.mvp_login_activity.domain.LoginData
import kotlin.math.log

private const val MOCK_LOADING_DURATION = 3000L
private const val SHORT_LOGIN_ERROR = 0
private const val INVALID_SYMBOLS_IN_LOGIN_ERROR = 1
private const val LOGIN_NOT_EXIST_ERROR = 2
private const val REGISTRATION_MESSAGE = "Вы будете перемещены на страницу регистрации."
private const val FORGOT_PASSWORD_MESSAGE =
    "На почту, указанную при регистрации выслан новый пароль для входа."

class LoginPresenter(
    private val router: Router,
) : Contract.Presenter() {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setState(Contract.ViewState.IDLE)
    }

    override fun onLoginRequest(loginData: LoginData) {
        viewState.setState(Contract.ViewState.LOADING)
        Handler(Looper.getMainLooper()).postDelayed({
            viewState.setState(Contract.ViewState.SUCCESS)
            router.exit()
            router.navigateTo(Screens.Main(loginData))
        }, MOCK_LOADING_DURATION)
    }

    override fun onRegistration(loginData: LoginData) {
        viewState.setState(Contract.ViewState.LOADING)
        Handler(Looper.getMainLooper()).postDelayed({
            viewState.setState(Contract.ViewState.SUCCESS)
            viewState.setMessage(REGISTRATION_MESSAGE)
        }, MOCK_LOADING_DURATION)
    }

    override fun onForgotPassword(loginData: LoginData) {
        viewState.setState(Contract.ViewState.LOADING)
        Handler(Looper.getMainLooper()).postDelayed({
            viewState.setState(Contract.ViewState.SUCCESS)
            viewState.setMessage(FORGOT_PASSWORD_MESSAGE)
        }, MOCK_LOADING_DURATION)
    }

    override fun onChangeLogin(login: String) {
        when {
            login.length < 3 -> viewState.setLoginError(SHORT_LOGIN_ERROR)
            !login.matches("[^\\W]+".toRegex()) -> viewState.setLoginError(
                INVALID_SYMBOLS_IN_LOGIN_ERROR)
            login !in listOf("admin", "user01", "guest") -> viewState.setLoginError(
                LOGIN_NOT_EXIST_ERROR)
            else -> viewState.setLoginAllow(true)
        }
    }
}