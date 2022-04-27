package ru.s1aks.mvp_login_activity.domain.interactor.login

interface IUserLoginInteractor {
    fun login(login: String, password: String, callback: (Int) -> Unit)
    fun logout(login: String?, callback: (Int) -> Unit)
}