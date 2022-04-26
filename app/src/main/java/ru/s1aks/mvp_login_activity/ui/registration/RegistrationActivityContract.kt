package ru.s1aks.mvp_login_activity.ui.registration

class RegistrationActivityContract {
    interface RegistrationView {
        fun setRegistrationSuccess(login: String)
        fun setRegistrationError(error: String)
        fun showProgress()
        fun hideProgress()
    }

    interface RegistrationPresenter {
        fun onAttach(mView: RegistrationView)
        fun onRegister(login: String, password: String)
    }
}