package ru.s1aks.mvp_login_activity.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import ru.s1aks.mvp_login_activity.App
import ru.s1aks.mvp_login_activity.R
import ru.s1aks.mvp_login_activity.databinding.ActivityLoginBinding
import ru.s1aks.mvp_login_activity.ui.registration.RegistrationActivity
import ru.s1aks.mvp_login_activity.ui.uiutils.hideKeyboard
import ru.s1aks.mvp_login_activity.ui.uiutils.showSnack

class LoginActivity : AppCompatActivity(), LoginActivityContract.LoginView {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var loginPresenter: LoginActivityPresenter

    companion object {
        const val EXTRA_LOGIN_REGISTRATION_SUCCESS = "EXTRA_LOGIN_SUCCESS"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val registrationActivityIntent = Intent(this, RegistrationActivity::class.java)

        with(binding) {
            intent.getStringExtra(EXTRA_LOGIN_REGISTRATION_SUCCESS)?.let {
                val registeredLogin = it
                loginTextEdit.setText(registeredLogin)
                root.showSnack(getString(R.string.registration_success, registeredLogin))
                intent.removeExtra(EXTRA_LOGIN_REGISTRATION_SUCCESS)
            }

            loginPresenter = restorePresenter().apply { onAttach(this@LoginActivity) }

            registrationButton.setOnClickListener {
                startActivity(registrationActivityIntent)
                finish()
            }

            loginButton.setOnClickListener {
                loginPresenter.onLogin(
                    loginTextEdit.text.toString(),
                    passwordTextEdit.text.toString()
                )
            }

            forgotPasswordButton.setOnClickListener {
                loginPresenter.onPasswordRemind(loginTextEdit.text.toString())
            }

            logoutButton.setOnClickListener {
                loginPresenter.onLogout()
            }
        }
    }

    private fun restorePresenter(): LoginActivityPresenter {
        val presenter = lastCustomNonConfigurationInstance as? LoginActivityPresenter
        return presenter ?: LoginActivityPresenter(App.userRepository,
            App.userLoginInteractor,
            App.userRemindPasswordInteractor,
            true)
    }

    @Deprecated("Deprecated in Java")
    override fun onRetainCustomNonConfigurationInstance(): Any {
        return loginPresenter
    }

    override fun setLoginSuccess(login: String) {
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

    override fun setAdminLoginSuccess() {
        with(binding) {
            adminGroup.isVisible = true
            loginPresenter.onGetUserList()
            with(adminLayout) {
                deleteUserAdminButton.setOnClickListener {
                    loginPresenter.onDeleteUser(targetUserLoginAdminTextEdit.text.toString())
                    it.hideKeyboard()
                }

                getUserAdminButton.setOnClickListener {
                    loginPresenter.onGetUser(targetUserLoginAdminTextEdit.text.toString())
                    it.hideKeyboard()
                }

                saveChangesAdminButton.setOnClickListener {
                    loginPresenter.onUpdateUser(
                        userIdAdminTextView.text.toString(),
                        newLoginAdminTextEdit.text.toString(),
                        newPasswordAdminTextEdit.text.toString()
                    )
                    it.hideKeyboard()
                }
            }
        }
    }

    override fun showMessage(message: String) {
        binding.root.apply {
            hideKeyboard()
            showSnack(message)
        }
    }

    override fun receiveUser(login: String, password: String, id: String) {
        with(binding.adminLayout) {
            targetUserLoginAdminTextEdit.setText(login)
            userIdAdminTextView.text = id
            newLoginAdminTextEdit.setText(login)
            newPasswordAdminTextEdit.setText(password)
        }
    }

    override fun setLogout() {
        with(binding) {
            helloUserTextView.text = getString(R.string.empty_text)
            adminLayout.userListAdminTextView.text = getString(R.string.empty_text)
            authorizedGroup.isVisible = false
            adminGroup.isVisible = false
            loginGroup.isVisible = true
        }
    }

    override fun showUserList(userList: String) {
        binding.adminLayout.userListAdminTextView.text = userList
    }

    override fun showRemindedPassword(remindedPassword: String) {
        binding.root.apply {
            hideKeyboard()
            showSnack(remindedPassword)
        }
    }

    override fun showProgress() {
        binding.progressBar.apply {
            hideKeyboard()
            isVisible = true
        }
    }

    override fun hideProgress() {
        binding.progressBar.isVisible = false
    }
}