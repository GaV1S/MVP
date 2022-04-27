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

    private lateinit var currentLogin: String
    private var isAppStart = true

    override val showProgress: Publisher<Boolean> = Publisher()

    override val isLoginSuccess: Publisher<String> = Publisher()

    override val isLogout: Publisher<Boolean> = Publisher()

    override val receivedUser: Publisher<UserEntity> = Publisher()

    override val receivedUserList: Publisher<String> = Publisher()

    override val messenger: Publisher<String> = Publisher()


    override fun onCheckOnAppStartAuthorization() {
        if (isAppStart) {
            showProgress.post(true)
            userRepository.getAllUsers { userList ->
                for(user in userList) {
                    if (user.isAuthorized) {
                        isLoginSuccess.post(user.userLogin)
                        currentLogin = user.userLogin
                        break
                    }
                }
                showProgress.post(false)
                isAppStart = false
            }
        }
    }

    override fun onLogin(login: String, password: String) {
        if (login.isBlank()) {
            messenger.post(MessageSource.LOGIN_CANNOT_BE_BLANK.message)
        } else {
            showProgress.post(true)
            userLoginInteractor.login(login, password) { response ->
                when (response) {
                    ResponseCodes.RESPONSE_SUCCESS.code -> {
                        showProgress.post(false)
                        isLoginSuccess.post(login)
                        currentLogin = login
                    }

                    ResponseCodes.RESPONSE_LOGIN_NOT_REGISTERED.code -> {
                        showProgress.post(false)
                        messenger.post(MessageSource.LOGIN_NOT_REGISTERED.message)
                    }

                    ResponseCodes.RESPONSE_INVALID_PASSWORD.code -> {
                        showProgress.post(false)
                        messenger.post(MessageSource.INVALID_PASSWORD.message)
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
                    messenger.post(MessageSource.LOGIN_NOT_REGISTERED.message)
                }

                ResponseCodes.RESPONSE_SUCCESS.code -> {
                    isLogout.post(true)
                    currentLogin = ""
                    showProgress.post(false)
                }
            }
        }
    }

    override fun onPasswordRemind(login: String) {
        if (login.isBlank()) {
            messenger.post(MessageSource.LOGIN_CANNOT_BE_BLANK.message)
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
            messenger.post(MessageSource.LOGIN_CANNOT_BE_BLANK.message)
        } else {
            showProgress.post(true)
            userRepository.getUser(login) { user ->
                if (user == null) {
                    messenger.post(MessageSource.LOGIN_NOT_REGISTERED.message)
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

    override fun onDeleteUser(login: String) {
        when {
            login.isBlank() -> {
                messenger.post(MessageSource.LOGIN_CANNOT_BE_BLANK.message)
            }

            login == DEFAULT_ADMIN_LOGIN -> {
                messenger.post(MessageSource.FORBIDDEN_TO_DELETE.message)
            }

            else -> {
                showProgress.post(true)
                userRepository.deleteUser(login) { response ->
                    when (response) {
                        ResponseCodes.RESPONSE_LOGIN_NOT_REGISTERED.code -> {
                            messenger.post(MessageSource.LOGIN_NOT_REGISTERED.message)
                            showProgress.post(false)
                        }
                        ResponseCodes.RESPONSE_SUCCESS.code -> {
                            onGetUserList()
                            messenger.post(MessageSource.ACCOUNT_DELETED.message)
                            showProgress.post(false)
                        }
                        ResponseCodes.RESPONSE_USER_DELETE_FAILED.code -> {
                            messenger.post(MessageSource.DATABASE_ACCESS_FAILED.message)
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
                messenger.post(MessageSource.YOU_HAVE_TO_LOAD_USER_DATA.message)
            }

            login.isBlank() -> {
                messenger.post(MessageSource.LOGIN_CANNOT_BE_BLANK.message)
            }

            password.isBlank() -> {
                messenger.post(MessageSource.PASSWORD_CANNOT_BE_BLANK.message)
            }

            login == DEFAULT_ADMIN_LOGIN -> {
                messenger.post(MessageSource.FORBIDDEN_TO_UPDATE.message)
            }

            else -> {
                showProgress.post(true)
                userRepository.updateUser(userId, login, password, false) { response ->
                    when (response) {
                        ResponseCodes.RESPONSE_SUCCESS.code -> {
                            onGetUserList()
                            messenger.post(MessageSource.ACCOUNT_UPDATED.message)
                            receivedUser.post(UserEntity(userId, login, password))
                            showProgress.post(false)
                        }
                        ResponseCodes.RESPONSE_USER_UPDATE_FAILED.code -> {
                            messenger.post(MessageSource.DATABASE_ACCESS_FAILED.message)
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

private enum class MessageSource(val message: String) {
    ACCOUNT_UPDATED("Учетная запись обновлена"),
    DATABASE_ACCESS_FAILED("Ошибка доступа к базе данных"),
    FORBIDDEN_TO_UPDATE("Невозможно редактировать \"${DEFAULT_ADMIN_LOGIN}\""),
    FORBIDDEN_TO_DELETE("Невозможно удалить \"${DEFAULT_ADMIN_LOGIN}\""),
    PASSWORD_CANNOT_BE_BLANK("Пароль не может быть пустым"),
    INVALID_PASSWORD("Неверный пароль"),
    LOGIN_CANNOT_BE_BLANK("Логин не может быть пустым"),
    LOGIN_NOT_REGISTERED("Логин не зарегистрирован"),
    ACCOUNT_DELETED("Учетная запись удалена"),
    YOU_HAVE_TO_LOAD_USER_DATA("Необходимо загрузить данные")
}