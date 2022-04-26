package ru.s1aks.mvp_login_activity.ui.login

class LoginActivityContract {
    interface LoginView {
        fun hideProgress()
        fun receiveUser(login: String, password: String, id: String)
        fun setLoginSuccess(login: String)
        fun setAdminLoginSuccess()
        fun setLogout()
        fun showMessage(message: String)
        fun showProgress()
        fun showRemindedPassword(remindedPassword: String)
        fun showUserList(userList: String)
    }

    interface LoginPresenter {
        fun onAttach(mView: LoginView)
        fun onDeleteUser(login: String)
        fun onGetUser(login: String)
        fun onGetUserList()
        fun onLogin(login: String, password: String)
        fun onLogout()
        fun onPasswordRemind(login: String)
        fun onUpdateUser(userId: String, login: String, password: String)
    }
}