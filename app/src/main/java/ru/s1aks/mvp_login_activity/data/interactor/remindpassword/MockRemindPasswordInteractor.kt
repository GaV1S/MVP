package ru.s1aks.mvp_login_activity.data.interactor.remindpassword

import android.os.Handler
import ru.s1aks.mvp_login_activity.domain.api.IUserDatabaseApi
import ru.s1aks.mvp_login_activity.domain.interactor.remindpassword.IRemindPasswordInteractor

class MockRemindPasswordInteractor(
    private val userDatabaseApi: IUserDatabaseApi,
    private val uiHandler: Handler
) : IRemindPasswordInteractor {
    override fun remindUserPassword(login: String, callback: (String) -> Unit) {
        Thread {
            uiHandler.post {
                callback(userDatabaseApi.remindUserPassword(login))
            }
        }.start()
    }
}