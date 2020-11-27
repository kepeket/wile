package com.wile.app.ui.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.wile.app.R
import com.wile.app.base.DataBindingActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsActivity : DataBindingActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings_fragment, SettingsFragment())
            .commit()
    }

    companion object {
        fun newIntent(context: Context) = Intent(context, SettingsActivity::class.java)
    }
}