package com.rpandey.covid19tracker_india.data.processor

import androidx.room.Transaction
import com.rpandey.covid19tracker_india.data.model.Country
import com.rpandey.covid19tracker_india.data.model.covidIndia.DistrictData
import com.rpandey.covid19tracker_india.data.model.covidIndia.DistrictResponse
import com.rpandey.covid19tracker_india.database.entity.DistrictEntity
import com.rpandey.covid19tracker_india.database.provider.CovidDatabase

class DistrictDataProcessor(covidDatabase: CovidDatabase) :
    ResponseProcessor<List<DistrictResponse>>(covidDatabase) {

    override fun process(data: List<DistrictResponse>) {
        val districtEntities = mutableListOf<DistrictEntity>()

        data.forEach {
            val stateName = it.state?.trim() ?: ""
            it.districtData?.forEach { districtData ->
                val district = districtData.district.trim()
                districtEntities.add(
                    DistrictEntity(
                        getDistrictId(stateName, district),
                        Country.INDIA.code,
                        stateName,
                        district,
                        districtData.confirmed
                    )
                )
            }
        }

        persistData(districtEntities)
    }

    private fun getDistrictId(state: String, district: String): Int {
        return (state + district).hashCode()
    }

    @Transaction
    private fun persistData(districtEntities: MutableList<DistrictEntity>) {
        covidDatabase.districtDao().delete()
        covidDatabase.districtDao().insert(districtEntities)
    }
}