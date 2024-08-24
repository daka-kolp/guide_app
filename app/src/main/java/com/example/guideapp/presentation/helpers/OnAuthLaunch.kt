package com.example.guideapp.presentation.helpers

import android.content.Intent

interface OnAuthLaunch {
    fun signIn(intent: Intent)
    fun showContent()
}
