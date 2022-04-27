package ru.s1aks.mvp_login_activity.ui.registration

import ru.s1aks.mvp_login_activity.ui.utils.Publisher

interface RegistrationViewModelContract {
    val isInProgress: Publisher<Boolean>
    val registrationSuccess: Publisher<String>
    val messenger: Publisher<Pair<Int, Any?>>

    fun onRegister(login: String, password: String)
}