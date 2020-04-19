package com.rpandey.covid19tracker_india.ui.search

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.rpandey.covid19tracker_india.R
import com.rpandey.covid19tracker_india.database.entity.DistrictEntity

class SearchDistrictActivity : AppCompatActivity(), SearchDistrictFragment.Callback {

    companion object {
        const val KEY_DISTRICT_ID = "KEY_DISTRICT_ID"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_search_activity)
        attachSearchFragment()
    }

    private fun attachSearchFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, SearchDistrictFragment(), SearchDistrictFragment.TAG)
            .commitAllowingStateLoss()
    }

    override fun onDistrictSelected(districtEntity: DistrictEntity) {
        val data = Intent().putExtra(
            KEY_DISTRICT_ID, districtEntity.districtId
        )
        setResult(Activity.RESULT_OK, data)
        finish()
    }
}