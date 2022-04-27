package ru.s1aks.mvp_login_activity

import android.app.Application
import android.content.Context
import android.os.Handler
import android.os.Looper
import ru.s1aks.mvp_login_activity.data.api.MockUserDatabaseApi
import ru.s1aks.mvp_login_activity.data.db.UserDatabase
import ru.s1aks.mvp_login_activity.data.interactor.login.MockUserLoginInteractor
import ru.s1aks.mvp_login_activity.data.interactor.registration.MockUserRegistrationInteractor
import ru.s1aks.mvp_login_activity.data.interactor.remindpassword.MockRemindPasswordInteractor
import ru.s1aks.mvp_login_activity.data.repository.MockUserDatabaseRepository
import ru.s1aks.mvp_login_activity.domain.api.IUserDatabaseApi
import ru.s1aks.mvp_login_activity.domain.interactor.login.IUserLoginInteractor
import ru.s1aks.mvp_login_activity.domain.interactor.registration.IUserRegistrationInteractor
import ru.s1aks.mvp_login_activity.domain.interactor.remindpassword.IRemindPasswordInteractor
import ru.s1aks.mvp_login_activity.domain.repository.IUserDatabaseRepository

class App : Application() {

        override fun onCreate() {
                super.onCreate()
                applicationInstance = this
                appContext = this.applicationContext
        }

        companion object {
                private lateinit var applicationInstance: App
                private lateinit var appContext: Context
                private val uiHandler = Handler(Looper.getMainLooper())

                private val userDatabase: UserDatabase by lazy {
                        UserDatabase.getUserDatabase(appContext)
                }

                private val userDatabaseApi: IUserDatabaseApi by lazy {
                        MockUserDatabaseApi(userDatabase.userDao())
                }

                val userRepository: IUserDatabaseRepository by lazy {
                        MockUserDatabaseRepository(userDatabase.userDao(), uiHandler)
                }

                val userLoginInteractor: IUserLoginInteractor by lazy {
                        MockUserLoginInteractor(userDatabaseApi, uiHandler)
                }
                val userRemindPasswordInteractor: IRemindPasswordInteractor by lazy {
                        MockRemindPasswordInteractor(userDatabaseApi, uiHandler)
                }
                val userRegistrationInteractor: IUserRegistrationInteractor by lazy {
                        MockUserRegistrationInteractor(userRepository)
                }
        }
}