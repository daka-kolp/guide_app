package com.example.guideapp.presentation.helpers

import android.content.Intent

interface OnAuthLaunch {
    fun login(intent: Intent)
    fun logout()
    fun showContent()
}
