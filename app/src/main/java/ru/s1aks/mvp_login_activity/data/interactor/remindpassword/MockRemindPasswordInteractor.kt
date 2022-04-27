package ru.s1aks.mvp_login_activity.data.interactor.remindpassword

import ru.s1aks.mvp_login_activity.domain.api.IUserDatabaseApi
import ru.s1aks.mvp_login_activity.domain.interactor.remindpassword.IRemindPasswordInteractor

class MockRemindPasswordInteractor(
    private val userDatabaseApi: IUserDatabaseApi,
) : IRemindPasswordInteractor {
    override fun remindUserPassword(login: String, callback: (Any) -> Unit) {
        Thread {
            callback(userDatabaseApi.remindUserPassword(login))
        }.start()
    }
}