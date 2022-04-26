package ru.s1aks.mvp_login_activity.data.interactor.registration

import ru.s1aks.mvp_login_activity.domain.interactor.registration.IUserRegistrationInteractor
import ru.s1aks.mvp_login_activity.domain.repository.IUserDatabaseRepository

class MockUserRegistrationInteractor(
    private val userRepository: IUserDatabaseRepository,
) : IUserRegistrationInteractor {
    override fun userRegistration(login: String, password: String, callback: (Int) -> Unit) {
        userRepository.addUser(login, password) { responseCode ->
            callback(responseCode)
        }
    }
}