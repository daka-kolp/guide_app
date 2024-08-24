package com.example.guideapp.presentation.fragments.content.result

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.guideapp.R

class ResultsFragment : Fragment() {
    private val viewModel by viewModels<ResultsViewModel>()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ResultsRecycleViewAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_result, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.results_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(view.context)
    }
}
