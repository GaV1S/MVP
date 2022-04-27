package ru.s1aks.mvp_login_activity.domain.interactor.remindpassword

interface IRemindPasswordInteractor {
    fun remindUserPassword(login: String, callback: (Any) -> Unit)
}