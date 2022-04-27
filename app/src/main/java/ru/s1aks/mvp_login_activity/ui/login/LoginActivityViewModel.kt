package ru.s1aks.mvp_login_activity.ui.login

import ru.s1aks.mvp_login_activity.data.db.UserEntity
import ru.s1aks.mvp_login_activity.domain.interactor.login.IUserLoginInteractor
import ru.s1aks.mvp_login_activity.domain.interactor.remindpassword.IRemindPasswordInteractor
import ru.s1aks.mvp_login_activity.domain.repository.IUserDatabaseRepository
import ru.s1aks.mvp_login_activity.ui.utils.Publisher

private const val DEFAULT_ADMIN_LOGIN = "admin"

class LoginActivityViewModel(
    private val userRepository: IUserDatabaseRepository,
    private val userLoginInteractor: IUserLoginInteractor,
    private val userRemindPasswordInteractor: IRemindPasswordInteractor,
) :
    LoginActivityContract.LoginViewModel {

    private var isLoginSuccessFlag = false
    private lateinit var currentLogin: String

    init {
        checkIsLoginSuccess()
    }


//    override fun onAttach(mView: LoginActivityContract.LoginView) {
//        view = mView
//
//        if (!isFirstAttach) {
//            checkIsLoginSuccess()
//        } else {
//            showProgress.post(true)
//            userRepository.getAllUsers { userList ->
//                for (user in userList) {
//                    if (user.isAuthorized) {
//                        isLoginSuccessFlag = true
//                        currentLogin = user.userLogin
//                        break
//                    }
//                }
//                checkIsLoginSuccess()
//                showProgress.post(false)
//            }
//            isFirstAttach = false
//        }
//    }

    private fun checkIsLoginSuccess() {
        if (isLoginSuccessFlag) {
            isLoginSuccess.post(currentLogin)
        }
    }

    override fun onLogin(login: String, password: String) {
        if (login.isBlank()) {
            messenger.post("getRe.getString(R.string.login_can_not_be_blank)")
        } else {
            showProgress.post(true)
            userLoginInteractor.login(login, password) { response ->
                when (response) {
                    ResponseCodes.RESPONSE_SUCCESS.code -> {
                        showProgress.post(false)
                        isLoginSuccess.post(login)
                        isLoginSuccessFlag = true
                        currentLogin = login
                    }

                    ResponseCodes.RESPONSE_LOGIN_NOT_REGISTERED.code -> {
                        showProgress.post(false)
                        messenger.post("Логин не зарегистрирован")
                    }

                    ResponseCodes.RESPONSE_INVALID_PASSWORD.code -> {
                        showProgress.post(false)
                        messenger.post("Неверный пароль")
                    }
                }
            }
        }
    }

    override fun onLogout() {
        showProgress.post(true)
        userLoginInteractor.logout(currentLogin) { response ->
            when (response) {
                ResponseCodes.RESPONSE_LOGIN_NOT_REGISTERED.code -> {
                    showProgress.post(false)
                    messenger.post("Логин не зарегистрирован")
                }

                ResponseCodes.RESPONSE_SUCCESS.code -> {
                    isLogout.post(true)
                    isLoginSuccessFlag = false
                    currentLogin = ""
                    showProgress.post(false)
                }
            }
        }
    }

    override fun onPasswordRemind(login: String) {
        if (login.isBlank()) {
            messenger.post("getRe.getString(R.string.login_can_not_be_blank)")
        } else {
            showProgress.post(true)
            userRemindPasswordInteractor.remindUserPassword(login) { response ->
                messenger.post(response)
                showProgress.post(false)
            }
        }
    }

    override fun onGetUser(login: String) {
        if (login.isBlank()) {
            messenger.post("getRe.getString(R.string.login_can_not_be_blank)")
        } else {
            showProgress.post(true)
            userRepository.getUser(login) { user ->
                if (user == null) {
                    messenger.post("Логин не зарегистрирован")
                    showProgress.post(false)
                } else {
                    receivedUser.post(user)
                    showProgress.post(false)
                }
            }
        }
    }

    override fun onGetUserList() {
        showProgress.post(true)
        userRepository.getAllUsers { mUserList ->
            if (mUserList.isNotEmpty()) {
                val userList = StringBuilder()
                for (user in mUserList) {
                    userList.append(user.userLogin)
                    userList.append(" : ")
                    userList.append(user.userPassword)
                    userList.append(" : ")
                    userList.append(user.isAuthorized)
                    userList.append("\n")
                    userList.append("----------\n")
                }
                messenger.post(userList.toString())
                showProgress.post(false)
            } else {
                messenger.post("")
                showProgress.post(false)
            }
        }
    }

    override val showProgress: Publisher<Boolean> = Publisher()

    override val isLoginSuccess: Publisher<String> = Publisher()

    override val isLogout: Publisher<Boolean> = Publisher()

    override val receivedUser: Publisher<UserEntity> = Publisher()

    override val receivedUserList: Publisher<String> = Publisher()

    override val messenger: Publisher<String> = Publisher()


    override fun onDeleteUser(login: String) {
        when {
            login.isBlank() -> {
                messenger.post("getRe.getString(R.string.login_can_not_be_blank)")
            }

            login == DEFAULT_ADMIN_LOGIN -> {
                messenger.post("Невозможно удалить \"${DEFAULT_ADMIN_LOGIN}\"")
            }

            else -> {
                showProgress.post(true)
                userRepository.deleteUser(login) { response ->
                    when (response) {
                        ResponseCodes.RESPONSE_LOGIN_NOT_REGISTERED.code -> {
                            messenger.post("Логин не зарегистрирован")
                            showProgress.post(false)
                        }
                        ResponseCodes.RESPONSE_SUCCESS.code -> {
                            onGetUserList()
                            messenger.post("Логин удален")
                            showProgress.post(false)
                        }
                        ResponseCodes.RESPONSE_USER_DELETE_FAILED.code -> {
                            messenger.post("Ошибка доступа к базе данных")
                            showProgress.post(false)
                        }
                    }
                }
            }
        }
    }

    override fun onUpdateUser(userId: Int, login: String, password: String) {
        when {
            userId.toString().isBlank() -> {
                messenger.post("Необходимо загрузить данные")
            }

            login.isBlank() -> {
                messenger.post("getRe.getString(R.string.login_can_not_be_blank)")
            }

            password.isBlank() -> {
                messenger.post("Пароль не может быть пустым")
            }

            login == DEFAULT_ADMIN_LOGIN -> {
                messenger.post("Невозможно редактировать \"${DEFAULT_ADMIN_LOGIN}\"")
            }

            else -> {
                showProgress.post(true)
                userRepository.updateUser(userId, login, password, false) { response ->
                    when (response) {
                        ResponseCodes.RESPONSE_SUCCESS.code -> {
                            onGetUserList()
                            messenger.post("Учетная запись обновлена")
                            receivedUser.post(UserEntity(userId, login, password))
                            showProgress.post(false)
                        }
                        ResponseCodes.RESPONSE_USER_UPDATE_FAILED.code -> {
                            messenger.post("Ошибка доступа к базе данных")
                            showProgress.post(false)
                        }
                    }
                }
            }
        }
    }
}

private enum class ResponseCodes(val code: Int) {
    RESPONSE_SUCCESS(200),
    RESPONSE_INVALID_PASSWORD(403),
    RESPONSE_LOGIN_NOT_REGISTERED(404),
    RESPONSE_USER_UPDATE_FAILED(454),
    RESPONSE_USER_DELETE_FAILED(464)
}