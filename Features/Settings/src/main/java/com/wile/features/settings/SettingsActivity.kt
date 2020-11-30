package com.wile.features.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.wile.core.databinding.DataBindingActivity
import com.wile.core.extensions.replaceFragment
import com.wile.features.settings.databinding.ActivitySettingsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsActivity : DataBindingActivity() {

    private val binding: ActivitySettingsBinding by binding(R.layout.activity_settings)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        supportFragmentManager.replaceFragment(
                R.id.settings_fragment,
                SettingsFragment.newInstance(),
                SettingsFragment.TAG
        )
    }

    companion object {
        fun newIntent(context: Context) = Intent(context, SettingsActivity::class.java)
    }
}