package com.example.cacatoid

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SearchViewModel : ViewModel() {
    var selectedPuzzle: Int = 71

    private val _running = MutableLiveData(false)
    val running: LiveData<Boolean> get() = _running

    private val _currentKey = MutableLiveData("")
    val currentKey: LiveData<String> get() = _currentKey

    private val _stats = MutableLiveData("")
    val stats: LiveData<String> get() = _stats

    private val _foundKey = MutableLiveData<Triple<String, String, String>?>()
    val foundKey: LiveData<Triple<String, String, String>?> get() = _foundKey

    private val listener = object : NativeBridge.SearchListener {
        override fun onStats(currentKeyHex: String, keysPerSec: Long, totalChecked: Long) {
            _currentKey.postValue(currentKeyHex)
            _stats.postValue("${keysPerSec.fmt()}/s  |  total: ${totalChecked.fmt()}")
        }
        override fun onFound(privHex: String, wif: String, address: String, puzzle: Int) {
            _foundKey.postValue(Triple(privHex, wif, address))
            _running.postValue(false)
        }
    }

    fun start() {
        _foundKey.value = null
        _running.value = true
        NativeBridge.nativeStart(selectedPuzzle, listener)
    }

    fun stop() {
        NativeBridge.nativeStop()
        _running.value = false
    }

    override fun onCleared() { NativeBridge.nativeStop() }

    private fun Long.fmt(): String = when {
        this >= 1_000_000 -> "%.1fM".format(this / 1_000_000.0)
        this >= 1_000     -> "%.1fK".format(this / 1_000.0)
        else              -> toString()
    }
}
