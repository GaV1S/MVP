package ru.s1aks.mvp_login_activity

import com.github.terrakok.cicerone.androidx.ActivityScreen
import ru.s1aks.mvp_login_activity.domain.LoginData
import ru.s1aks.mvp_login_activity.ui.MainActivity

object Screens {
    fun Main(data: LoginData) = ActivityScreen { MainActivity.createLaunchIntent(it, data) }
}