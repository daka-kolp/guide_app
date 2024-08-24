package com.example.guideapp.presentation.fragments.content.content

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.viewModels
import com.example.guideapp.R
import com.example.guideapp.presentation.helpers.OnAuthLaunch
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ContentFragment : Fragment() {
    private val viewModel by viewModels<ContentViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_content, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val logoutButton = view.findViewById<Button>(R.id.logout_button)
        logoutButton.setOnClickListener { (requireActivity() as OnAuthLaunch).logout() }
    }
}
