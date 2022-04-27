package ru.s1aks.mvp_login_activity.data.interactor.login

import ru.s1aks.mvp_login_activity.domain.api.IUserDatabaseApi
import ru.s1aks.mvp_login_activity.domain.interactor.login.IUserLoginInteractor

class MockUserLoginInteractor(
    private val userDataBaseApi: IUserDatabaseApi,
) : IUserLoginInteractor {
    override fun login(login: String, password: String, callback: (Int) -> Unit) {
        Thread {
            callback(userDataBaseApi.login(login, password))
        }.start()
    }

    override fun logout(login: String, callback: (Int) -> Unit) {
        Thread {
            callback(userDataBaseApi.logout(login))
        }.start()
    }
}