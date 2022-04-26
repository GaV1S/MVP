package ru.s1aks.mvp_login_activity.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [UserEntity::class], version = 2, exportSchema = false)
abstract class UserDb : RoomDatabase() {
    abstract fun userDao() : UserDao
}