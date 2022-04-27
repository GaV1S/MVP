package ru.s1aks.mvp_login_activity.domain.interactor.registration

interface IUserRegistrationInteractor {
    fun userRegistration(login: String, password: String, callback:(Int) -> Unit)
}