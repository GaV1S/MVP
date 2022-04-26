package ru.s1aks.mvp_login_activity

import android.app.Application
import android.os.Handler
import android.os.Looper
import androidx.room.Room
import ru.s1aks.mvp_login_activity.data.api.MockUserDatabaseApi
import ru.s1aks.mvp_login_activity.data.db.UserDao
import ru.s1aks.mvp_login_activity.data.db.UserDb
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
                instance = this
        }

        companion object {
                private const val APP_DB_NAME = "Users.db"
                private val uiHandler = Handler(Looper.getMainLooper())
                private val userDatabaseApi: IUserDatabaseApi by lazy {
                        MockUserDatabaseApi(getUserDao())
                }
                private var instance: App? = null
                private var appDb: UserDb? = null
                val userRepository: IUserDatabaseRepository by lazy {
                        MockUserDatabaseRepository(getUserDao(), uiHandler)
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


                private fun getUserDao(): UserDao {
                        if (appDb == null) {
                                synchronized(UserDb::class.java) {
                                        if (appDb == null) {
                                                if (instance == null) throw IllegalAccessException("App is null")
                                                appDb = Room.databaseBuilder(
                                                        instance!!.applicationContext,
                                                        UserDb::class.java,
                                                        APP_DB_NAME
                                                )
                                                        .allowMainThreadQueries()
                                                        .fallbackToDestructiveMigration()
                                                        .build()
                                        }
                                }
                        }

                        return appDb!!.userDao()
                }
        }
}