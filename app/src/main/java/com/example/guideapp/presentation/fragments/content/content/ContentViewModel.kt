package com.example.guideapp.presentation.fragments.content.content

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.guideapp.core.domain.GuideRepository
import com.example.guideapp.core.domain.entities.Geolocation
import com.example.guideapp.core.domain.entities.Route
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ContentViewModel @Inject constructor(private val repository: GuideRepository) : ViewModel() {
    private val _uiDirectionsState = MutableLiveData<UIDirectionsState>(UIDirectionsState.Empty)
    val uiDirectionsState: LiveData<UIDirectionsState> = _uiDirectionsState

    fun getDirections(origin: Geolocation, destination: Geolocation) {
        _uiDirectionsState.value = UIDirectionsState.Processing
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                var value: UIDirectionsState = UIDirectionsState.Processing
                _uiDirectionsState.postValue(value)
                try {
                    val result = repository.getDirections(origin, destination)
                    value = UIDirectionsState.Result(result)
                } catch (e: Exception) {
                    value = UIDirectionsState.Error(e.localizedMessage ?: e.toString())
                } finally {
                    _uiDirectionsState.postValue(value)
                }
            }
        }
    }

    sealed class UIDirectionsState {
        data object Empty : UIDirectionsState()
        data object Processing : UIDirectionsState()
        class Result(val routes: List<Route>) : UIDirectionsState()
        class Error(val error: String) : UIDirectionsState()
    }
}
