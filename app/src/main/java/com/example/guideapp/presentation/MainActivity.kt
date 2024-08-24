package com.example.guideapp.presentation

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.guideapp.R
import com.example.guideapp.presentation.fragments.auth.AuthViewModel
import com.example.guideapp.presentation.fragments.content.ContentFragment
import com.example.guideapp.presentation.helpers.OnAuthLaunch
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), OnAuthLaunch {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
    }

    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val viewModel = ViewModelProvider(this)[AuthViewModel::class.java]
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

    override fun signIn(intent: Intent) {
        startForResult.launch(intent)
    }

    override fun showContent() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, ContentFragment())
            .commit()
    }
}
