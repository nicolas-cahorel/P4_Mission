package com.aura.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.aura.R
import com.aura.ui.login.LoginFragment

/**
 * MainActivity serves as the entry point of the application.
 * It hosts the main fragment container and initializes the initial fragment.
 */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, LoginFragment())
                .commit()
        }
    }
}