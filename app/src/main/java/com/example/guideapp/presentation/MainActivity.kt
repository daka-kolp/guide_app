package com.example.guideapp.presentation

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.guideapp.R
import com.example.guideapp.presentation.fragments.auth.AuthFragment
import com.example.guideapp.presentation.fragments.auth.AuthViewModel
import com.example.guideapp.presentation.fragments.content.content.ContentFragment
import com.example.guideapp.presentation.helpers.OnAuthLaunch
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), OnAuthLaunch {
    private val viewModel by viewModels<AuthViewModel>()
    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        onResultCallback(it)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
    }

    override fun login(intent: Intent) {
        startForResult.launch(intent)
    }

    override fun logout() {
        viewModel.logout()
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, AuthFragment())
            .commit()
    }

    override fun showContent() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, ContentFragment())
            .commit()
    }

    private fun onResultCallback(result: ActivityResult) {
        if (result.resultCode == Activity.RESULT_OK) {
            viewModel.login(
                result.data,
                onSuccess = { showContent() },
                onFailure = { error ->
                    Toast.makeText(this, "Error: $error", Toast.LENGTH_SHORT).show()
                }
            )
        } else {
            Toast.makeText(this, "Error: $result", Toast.LENGTH_SHORT).show()
        }
    }
}
