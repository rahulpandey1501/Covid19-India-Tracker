package com.rpandey.covid19tracker_india.data.processor

import androidx.room.Transaction
import com.rpandey.covid19tracker_india.data.model.Country
import com.rpandey.covid19tracker_india.data.model.covidIndia.DistrictResponse
import com.rpandey.covid19tracker_india.data.model.covidIndia.ZoneData
import com.rpandey.covid19tracker_india.database.entity.DistrictEntity
import com.rpandey.covid19tracker_india.database.provider.CovidDatabase

class DistrictDataProcessor(covidDatabase: CovidDatabase) :
    ResponseProcessor<Pair<List<DistrictResponse>, List<ZoneData>>>(covidDatabase) {

    override fun process(data: Pair<List<DistrictResponse>, List<ZoneData>>) {

        val districtList = data.first
        val zoneList = data.second
        val zoneDistrictMapping = zoneList.associateBy { it.district.trim() to it.stateCode.trim() }

        val districtEntities = mutableListOf<DistrictEntity>()

        districtList.forEach {
            val stateCode = it.stateCode?.trim() ?: ""
            val stateName = it.stateName?.trim() ?: ""

            it.districtData?.forEach { districtData ->
                try {
                    val district = districtData.district.trim()
                    val delta = districtData.delta
                    val zoneData = zoneDistrictMapping[district to stateCode]
                    val zone = if (zoneData?.zone.isNullOrEmpty()) null else zoneData?.zone
                    districtEntities.add(
                        DistrictEntity(
                            getDistrictId(stateName, district),
                            Country.INDIA.code,
                            stateName,
                            district,
                            delta.confirmed,
                            districtData.confirmed,
                            delta.recovered,
                            districtData.recovered,
                            delta.deceased,
                            districtData.deceased,
                            zone?.trim()
                        )
                    )
                } catch (e: Exception) {}
            }
        }

        persistData(districtEntities)
    }

    private fun getDistrictId(state: String, district: String): Int {
        return (state + district).hashCode()
    }

    @Transaction
    private fun persistData(districtEntities: MutableList<DistrictEntity>) {
//        covidDatabase.districtDao().delete()
        covidDatabase.districtDao().insert(districtEntities)
    }
}