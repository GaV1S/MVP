package ru.s1aks.mvp_login_activity.ui

import android.os.Bundle
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import com.github.terrakok.cicerone.androidx.AppNavigator
import com.google.android.material.snackbar.Snackbar
import moxy.MvpAppCompatActivity
import moxy.ktx.moxyPresenter
import ru.s1aks.mvp_login_activity.databinding.ActivityLoginBinding
import ru.s1aks.mvp_login_activity.domain.LoginData
import ru.s1aks.mvp_login_activity.data.utils.app

private const val MOCK_ERROR_MESSAGE: String = "Error"

class LoginActivity : MvpAppCompatActivity(), Contract.View {
    private lateinit var binding: ActivityLoginBinding
    private val presenter by moxyPresenter { LoginPresenter(app.router) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preInit()
        initView()
    }

    private fun preInit() {
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadState()
    }

    private fun loadState() {
        lastCustomNonConfigurationInstance?.let { setLoginData(it as LoginData) }
    }

    override fun onRetainCustomNonConfigurationInstance(): Any? {
        super.onRetainCustomNonConfigurationInstance()
        return packLoginData()
    }

    private fun initView() {
        setLoginAllow(false)
        initClickers()
        binding.loginEditText.addTextChangedListener {
            presenter.onChangeLogin(it.toString())
        }
    }

    private fun initClickers() {
        binding.loginButton.setOnClickListener {
            presenter.onLoginRequest(packLoginData())
        }
        binding.registrationButton.setOnClickListener {
            presenter.onRegistration(packLoginData())
        }
        binding.forgotPasswordButton.setOnClickListener {
            presenter.onForgotPassword(packLoginData())
        }

    }

    private fun packLoginData(): LoginData {
        return LoginData(
            binding.loginEditText.text.toString(),
            binding.passwordEditText.text.toString()
        )
    }

    private fun hideContent() {
        binding.loginPageProgressBar.isVisible = false
        binding.loginPageContentLayout.isVisible = false
    }

    override fun setState(state: Contract.ViewState) {
        hideContent()
        when (state) {
            Contract.ViewState.IDLE -> binding.loginPageContentLayout.isVisible = true
            Contract.ViewState.LOADING -> binding.loginPageProgressBar.isVisible = true
            Contract.ViewState.SUCCESS -> binding.loginPageSuccessImage.isVisible = true
            Contract.ViewState.ERROR -> {
                binding.loginPageContentLayout.isVisible = true
                setMessage(MOCK_ERROR_MESSAGE)
            }
        }
    }

    override fun setMessage(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    override fun setLoginError(errorCode: Int) {
        binding.loginEditText.error = getErrorStringByCode(errorCode)
        setLoginAllow(false)
    }

    override fun setLoginData(loginData: LoginData) {
        binding.loginEditText.setText(loginData.login)
        binding.passwordEditText.setText(loginData.password)
    }

    override fun setLoginAllow(loginAllowState: Boolean) {
        binding.loginButton.isEnabled = loginAllowState
    }

    private fun getErrorStringByCode(errorCode: Int): String {
        return when (errorCode) {
            0 -> "Логин слишком короткий"
            1 -> "Недопустимые символы в логине"
            2 -> "Логин не существует"
            else -> "Неизвестная ошибка"
        }
    }

    private val navigator by lazy { AppNavigator(this, binding.root.id) }

    override fun onResumeFragments() {
        super.onResumeFragments()
        app.navigatorHolder.setNavigator(navigator)
    }

    override fun onPause() {
        app.navigatorHolder.removeNavigator()
        super.onPause()
    }
}