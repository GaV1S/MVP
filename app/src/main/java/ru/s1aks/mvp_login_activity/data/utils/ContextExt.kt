package ru.s1aks.mvp_login_activity.data.utils

import android.content.Context
import ru.s1aks.mvp_login_activity.App

val Context.app : App
    get() {
        return applicationContext as App
    }