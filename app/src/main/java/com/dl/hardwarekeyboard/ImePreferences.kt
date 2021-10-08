package com.dl.hardwarekeyboard

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner

class ImePreferences : Activity(), AdapterView.OnItemSelectedListener {
    private lateinit var layoutsSpinner: Spinner
    private lateinit var shortcutsSpinner: Spinner

    private lateinit var preferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ime_preferences)

        preferences = application.
            getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)

        layoutsSpinner = findViewById(R.id.layouts_spinner)
        layoutsSpinner.setSelection(preferences.getInt(CURRENT_LAYOUT_PREFERENCE_KEY, 0))
        layoutsSpinner.onItemSelectedListener = this

        shortcutsSpinner = findViewById(R.id.shortcuts_spinner)
        shortcutsSpinner.setSelection(preferences.getInt(CURRENT_SHORTCUT_PREFERENCE_KEY, 0))
        shortcutsSpinner.onItemSelectedListener = this
    }

    @SuppressLint("ApplySharedPref")
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if(parent == layoutsSpinner) {
           preferences.edit().putInt(CURRENT_LAYOUT_PREFERENCE_KEY, position).commit()
        }

        if(parent == shortcutsSpinner) {
            preferences.edit().putInt(CURRENT_SHORTCUT_PREFERENCE_KEY, position).commit()
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }
}