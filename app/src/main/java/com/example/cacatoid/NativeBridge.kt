package com.example.cacatoid

object NativeBridge {
    init { System.loadLibrary("para o puzzle 71") }

    interface SearchListener {
        fun onStats(currentKeyHex: String, keysPerSec: Long, totalChecked: Long)
        fun onFound(privHex: String, wif: String, address: String, puzzle: Int)
    }

    external fun nativeStart(puzzle: Int, listener: SearchListener)
    external fun nativeStop()
}
