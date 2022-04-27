package ru.s1aks.mvp_login_activity.ui.login

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import ru.s1aks.mvp_login_activity.App
import ru.s1aks.mvp_login_activity.R
import ru.s1aks.mvp_login_activity.data.db.UserEntity
import ru.s1aks.mvp_login_activity.databinding.ActivityLoginBinding
import ru.s1aks.mvp_login_activity.ui.registration.RegistrationActivity
import ru.s1aks.mvp_login_activity.ui.utils.hideKeyboard
import ru.s1aks.mvp_login_activity.ui.utils.showSnack

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private var loginViewModel: LoginActivityContract.LoginViewModel? = null
    private val uiHandler = Handler(mainLooper)

    companion object {
        const val EXTRA_LOGIN_REGISTRATION_SUCCESS = "EXTRA_LOGIN_SUCCESS"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loginViewModel = restoreViewModel()

        loginViewModel?.receivedUser?.subscribe(uiHandler) { user ->
            user?.let {
                receiveUser(user)
            }
        }

        loginViewModel?.showProgress?.subscribe(uiHandler) { isInProgress ->
            isInProgress?.let {
                if (isInProgress) {
                    showProgress()
                } else {
                    hideProgress()
                }
            }
        }

        loginViewModel?.isLoginSuccess?.subscribe(uiHandler) { login ->
            login?.let {
                setLoginSuccess(login)
                if (login == "admin") {
                    setAdminLoginSuccess()
                }
            }
        }

        loginViewModel?.isLogout?.subscribe(uiHandler) { isLogout ->
            isLogout?.let {
                if (isLogout) {
                    setLogout()
                }
            }
        }

        loginViewModel?.receivedUserList?.subscribe(uiHandler) { userListAsString ->
            userListAsString?.let { showUserList(userListAsString) }
        }

        loginViewModel?.messenger?.subscribe(uiHandler) { message ->
            message?.let {
                showMessage(message)
            }
        }

        val registrationActivityIntent = Intent(this, RegistrationActivity::class.java)

        with(binding) {
            intent.getStringExtra(EXTRA_LOGIN_REGISTRATION_SUCCESS)?.let {
                val registeredLogin = it
                loginTextEdit.setText(registeredLogin)
                root.showSnack(getString(R.string.registration_success, registeredLogin))
                intent.removeExtra(EXTRA_LOGIN_REGISTRATION_SUCCESS)
            }



            registrationButton.setOnClickListener {
                startActivity(registrationActivityIntent)
                finish()
            }

            loginButton.setOnClickListener {
                loginViewModel?.onLogin(
                    loginTextEdit.text.toString(),
                    passwordTextEdit.text.toString()
                )
            }

            forgotPasswordButton.setOnClickListener {
                loginViewModel?.onPasswordRemind(loginTextEdit.text.toString())
            }

            logoutButton.setOnClickListener {
                loginViewModel?.onLogout()
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onRetainCustomNonConfigurationInstance(): Any? {
        return loginViewModel
    }

    override fun onDestroy() {
        super.onDestroy()
        loginViewModel?.isLoginSuccess?.unsubscribeAll()
    }

    private fun restoreViewModel(): LoginActivityViewModel {
        val loginViewModel = lastCustomNonConfigurationInstance as? LoginActivityViewModel
        return loginViewModel ?: LoginActivityViewModel(App.userRepository,
            App.userLoginInteractor,
            App.userRemindPasswordInteractor)
    }

    private fun setLoginSuccess(login: String) {
        with(binding) {
            root.hideKeyboard()
            authorizedGroup.isVisible = true
            loginGroup.isVisible = false
            adminGroup.isVisible = false
            helloUserTextView.text = getString(R.string.hello_user, login)
            loginTextEdit.text?.clear()
            passwordTextEdit.text?.clear()
        }
    }

    private  fun setAdminLoginSuccess() {
        with(binding) {
            adminGroup.isVisible = true
            loginViewModel?.onGetUserList()
            with(adminLayout) {
                deleteUserAdminButton.setOnClickListener {
                    loginViewModel?.onDeleteUser(targetUserLoginAdminTextEdit.text.toString())
                    it.hideKeyboard()
                }

                getUserAdminButton.setOnClickListener {
                    loginViewModel?.onGetUser(targetUserLoginAdminTextEdit.text.toString())
                    it.hideKeyboard()
                }

                saveChangesAdminButton.setOnClickListener {
                    loginViewModel?.onUpdateUser(
                        userIdAdminTextView.text.toString().toInt(),
                        newLoginAdminTextEdit.text.toString(),
                        newPasswordAdminTextEdit.text.toString()
                    )
                    it.hideKeyboard()
                }
            }
        }
    }

    private  fun showMessage(message: String) {
        binding.root.apply {
            hideKeyboard()
            showSnack(message)
        }
    }

    private  fun receiveUser(user: UserEntity) {
        with(binding.adminLayout) {
            targetUserLoginAdminTextEdit.setText(user.userLogin)
            userIdAdminTextView.text = user.userId.toString()
            newLoginAdminTextEdit.setText(user.userLogin)
            newPasswordAdminTextEdit.setText(user.userPassword)
        }
    }

    private  fun setLogout() {
        with(binding) {
            helloUserTextView.text = getString(R.string.empty_text)
            adminLayout.userListAdminTextView.text = getString(R.string.empty_text)
            authorizedGroup.isVisible = false
            adminGroup.isVisible = false
            loginGroup.isVisible = true
        }
    }

    private  fun showUserList(userList: String) {
        binding.adminLayout.userListAdminTextView.text = userList
    }

    private fun showProgress() {
        binding.progressBar.apply {
            hideKeyboard()
            isVisible = true
        }
    }

    private fun hideProgress() {
        binding.progressBar.isVisible = false
    }
}