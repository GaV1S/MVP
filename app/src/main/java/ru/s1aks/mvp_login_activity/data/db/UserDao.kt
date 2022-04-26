package ru.s1aks.mvp_login_activity.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun createUser(entity: UserEntity)

    @Query("SELECT * FROM UserEntity WHERE userLogin =:login")
    fun getUser(login: String): UserEntity?

    @Query("SELECT * FROM UserEntity")
    fun getAllUsers(): List<UserEntity>

    @Query("UPDATE UserEntity SET userLogin = :newLogin, userPassword = :newPassword, isAuthorized = :isAuthorized WHERE userId =:userId")
    fun updateUser(userId: String, newLogin: String, newPassword: String, isAuthorized: Boolean)

    @Query("DELETE FROM UserEntity WHERE userLogin == :login")
    fun deleteUser(login: String)

    @Query("UPDATE UserEntity SET isAuthorized = :isAuthorized WHERE userLogin =:login")
    fun userLogin(login: String, isAuthorized: Boolean = true)

    @Query("UPDATE UserEntity SET isAuthorized = :isAuthorized WHERE userLogin =:login")
    fun userLogout(login: String, isAuthorized: Boolean = false)
}