package ru.s1aks.mvp_login_activity.ui.registration

import ru.s1aks.mvp_login_activity.R
import ru.s1aks.mvp_login_activity.domain.interactor.registration.IUserRegistrationInteractor
import ru.s1aks.mvp_login_activity.ui.uiutils.UiConstants

class RegistrationActivityPresenter(private val userRegistrationInteractor: IUserRegistrationInteractor) :
    RegistrationActivityContract.RegistrationPresenter {

    private lateinit var view: RegistrationActivityContract.RegistrationView

    override fun onAttach(mView: RegistrationActivityContract.RegistrationView) {
        view = mView
    }

    override fun onRegister(login: String, password: String) {
        if (login.isBlank()) {
            view.setRegistrationError((view as RegistrationActivity).getString(R.string.login_can_not_be_blank))
        } else {
            view.showProgress()
            userRegistrationInteractor.userRegistration(login, password) { response ->
                when (response) {
                    UiConstants.ResponseCodes.RESPONSE_SUCCESS.code -> {
                        view.hideProgress()
                        view.setRegistrationSuccess(login)
                    }
                    UiConstants.ResponseCodes.RESPONSE_LOGIN_REGISTERED_YET.code -> {
                        view.hideProgress()
                        view.setRegistrationError(
                            (view as RegistrationActivity).getString(
                                R.string.login_registered_yet,
                                login
                            )
                        )
                    }
                }
            }
        }
    }
}