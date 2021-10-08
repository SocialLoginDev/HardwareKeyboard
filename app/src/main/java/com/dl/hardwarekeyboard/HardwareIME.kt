package com.dl.hardwarekeyboard

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.inputmethodservice.InputMethodService
import android.util.Log
import android.view.KeyEvent
import android.view.inputmethod.InputMethodSubtype
import android.widget.Toast
import com.beust.klaxon.Klaxon

class KeyMap(val key_code: Int, val letter: String, val letter_shift: String)

class HardwareIME : InputMethodService() {

    private var layout: List<KeyMap>? = null

    private lateinit var preferences: SharedPreferences

    override fun onCreate() {
        super.onCreate()

        val jsonSt = resources.openRawResource(R.raw.ru_layout)
        layout = Klaxon().parseArray<KeyMap>(jsonSt)
        jsonSt.close()

        preferences = application.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
    }

    @SuppressLint("ApplySharedPref")
    private fun changeLayout() {
        var curLayout = preferences.getInt(CURRENT_LAYOUT_PREFERENCE_KEY, 0)
        curLayout = if (curLayout == LAYOUT_RU) LAYOUT_EN else LAYOUT_RU
        preferences.edit().putInt(CURRENT_LAYOUT_PREFERENCE_KEY, curLayout).commit()

        val msg = getString(R.string.change_layout) + " " + resources.getStringArray(R.array.layouts)[curLayout]
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    private fun handleLayoutShortcut(keyCode: Int, event: KeyEvent?) {
        when (preferences.getInt(CURRENT_SHORTCUT_PREFERENCE_KEY, 0)) {
            LAYOUT_SHORTCUT_CTRL_SHIFT ->
                if ((event?.isShiftPressed == true) && event.isCtrlPressed) {
                    changeLayout()
                }

            LAYOUT_SHORTCUT_ALT_SHIFT ->
                if ((event?.isAltPressed == true) && event.isShiftPressed) {
                    changeLayout()
                }

            LAYOUT_SHORTCUT_ALT_SPACE ->
                if ((event?.isAltPressed == true) && (keyCode == KeyEvent.KEYCODE_SPACE)) {
                    changeLayout()
                }
        }
    }

    private fun handleCapsLock(key: KeyMap, isShiftPressed: Boolean) {
        if(key.letter.single().isLetter() && !isShiftPressed) {
            currentInputConnection.commitText(key.letter_shift, 1)
        } else {
            if(!key.letter.single().isLetter() && isShiftPressed) {
                currentInputConnection.commitText(key.letter_shift, 1)
            } else {
                currentInputConnection.commitText(key.letter, 1)
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        handleLayoutShortcut(keyCode, event)

        if (preferences.getInt(CURRENT_LAYOUT_PREFERENCE_KEY, 0) == LAYOUT_EN) {
            return false
        }

        if (event?.keyCharacterMap?.isPrintingKey(keyCode) == true) {
            val key = layout?.find { it.key_code == keyCode }
            if (key != null) {
                when {
                    event.isCapsLockOn -> {
                        handleCapsLock(key, event.isShiftPressed)
                    }
                    event.isShiftPressed -> {
                        currentInputConnection.commitText(key.letter_shift, 1)
                    }
                    else -> {
                        currentInputConnection.commitText(key.letter, 1)
                    }
                }
                return true
            }
        }
        return false
    }

    override fun onCurrentInputMethodSubtypeChanged(newSubtype: InputMethodSubtype?) {
        super.onCurrentInputMethodSubtypeChanged(newSubtype)
    }
}