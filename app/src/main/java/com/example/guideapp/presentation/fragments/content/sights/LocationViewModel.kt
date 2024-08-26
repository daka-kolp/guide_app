package com.example.guideapp.presentation.fragments.content.sights

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.guideapp.core.domain.entities.Geolocation
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor() : ViewModel() {
    val valueToUpdate = MutableLiveData<Geolocation?>(null)
    fun saveLocation(location: Geolocation?) {
        valueToUpdate.value = location
    }
}
