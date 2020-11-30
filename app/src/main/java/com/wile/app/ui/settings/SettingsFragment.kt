package com.wile.app.ui.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.wile.app.R

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }

    companion object {
        const val TAG = "SettingsFragment"
        fun newInstance() = SettingsFragment()
    }
}