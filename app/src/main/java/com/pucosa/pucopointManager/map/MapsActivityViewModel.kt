package com.pucosa.pucopointManager.map

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pucosa.pucopointManager.models.Pucopoint

class MapsActivityViewModel: ViewModel() {
    val pucopointsLiveData: MutableLiveData<List<Pucopoint>> = MutableLiveData(emptyList())

    init {

    }

    companion object {
        private const val TAG = "MapsActivityViewModel"
    }
}