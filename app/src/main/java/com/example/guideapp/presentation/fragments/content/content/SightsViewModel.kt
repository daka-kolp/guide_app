package com.example.guideapp.presentation.fragments.content.content

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.guideapp.core.domain.GuideRepository
import com.example.guideapp.core.domain.entities.Geolocation
import com.example.guideapp.core.domain.entities.Sight
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SightsViewModel @Inject constructor(private val repository: GuideRepository) : ViewModel() {
    private val _uiSightsState = MutableLiveData<UISightsState>(UISightsState.Empty)
    val uiSightsState: LiveData<UISightsState> = _uiSightsState

    fun getSights(origin: Geolocation) {
        _uiSightsState.value = UISightsState.Processing
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                var value: UISightsState = UISightsState.Processing
                _uiSightsState.postValue(value)
                try {
                    val result = repository.getSightsByUserLocation(origin)
                    value = UISightsState.Result(origin, result)
                } catch (e: Exception) {
                    value = UISightsState.Error(e.localizedMessage ?: e.toString())
                } finally {
                    _uiSightsState.postValue(value)
                }
            }
        }
    }

    sealed class UISightsState {
        data object Empty : UISightsState()
        data object Processing : UISightsState()
        class Result(val origin: Geolocation, val sights: List<Sight>) : UISightsState()
        class Error(val error: String) : UISightsState()
    }
}
