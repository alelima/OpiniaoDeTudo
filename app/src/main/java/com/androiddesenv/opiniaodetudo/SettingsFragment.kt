package com.androiddesenv.opiniaodetudo

import android.os.Bundle
import android.support.v7.preference.PreferenceFragmentCompat

class SettingsFragment : PreferenceFragmentCompat() {
    companion object {
        const val NIGHT_MODE_PREF = "night_mode_pref"
    }
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
        configNightMode()
    }
    private fun configNightMode() {
        val preference = preferenceManager.findPreference(NIGHT_MODE_PREF)
        preference.setOnPreferenceChangeListener { preference, newValue ->
            (activity as MainActivity).setNightMode()
            true
        }
    }
}