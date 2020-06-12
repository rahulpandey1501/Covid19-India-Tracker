package com.rpandey.covid19tracker_india.data.processor

import androidx.room.Transaction
import com.rpandey.covid19tracker_india.data.model.Country
import com.rpandey.covid19tracker_india.data.model.covidIndia.ZoneData
import com.rpandey.covid19tracker_india.database.entity.DistrictUpdate
import com.rpandey.covid19tracker_india.database.provider.CovidDatabase

class ZoneProcessor(covidDatabase: CovidDatabase) :
    ResponseProcessor<List<ZoneData>>(covidDatabase) {

    override fun process(data: List<ZoneData>) {
        val zoneData = mutableListOf<DistrictUpdate>()
        data.forEach {
            try {
                zoneData.add(
                    DistrictUpdate(
                        Country.INDIA.code,
                        it.stateName,
                        it.district,
                        it.zone
                    )
                )
            } catch (e: Exception) {
            }
        }
        persistData(zoneData)
    }

    @Transaction
    private fun persistData(districtEntities: MutableList<DistrictUpdate>) {
        covidDatabase.districtDao().updateZone(districtEntities)
    }
}