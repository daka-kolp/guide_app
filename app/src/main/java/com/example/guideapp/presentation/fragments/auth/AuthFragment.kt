package com.example.guideapp.presentation.fragments.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.guideapp.R
import com.example.guideapp.presentation.helpers.OnAuthLaunch
import com.google.android.gms.common.SignInButton
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_auth, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModel: AuthViewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        val activity = requireActivity() as OnAuthLaunch
        if(viewModel.getAccount() != null) activity.showContent()

        val signInButton: SignInButton = view.findViewById(R.id.sign_in_button)
        signInButton.setOnClickListener { activity.login(viewModel.getClient().signInIntent) }

    }
}
