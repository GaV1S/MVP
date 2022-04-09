package ru.s1aks.mvp_login_activity.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.s1aks.mvp_login_activity.databinding.ActivityMainBinding
import ru.s1aks.mvp_login_activity.domain.LoginData

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    companion object {
        private const val MAIN_EXTRA_KEY = "main_key"

        fun createLaunchIntent(context: Context, data: LoginData? = null): Intent {
            val intent = Intent(context, MainActivity::class.java)
            intent.putExtra(MAIN_EXTRA_KEY, data)
            return intent
        }
    }

}