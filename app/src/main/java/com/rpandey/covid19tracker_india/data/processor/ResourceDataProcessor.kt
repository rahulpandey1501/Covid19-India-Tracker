package com.rpandey.covid19tracker_india.data.processor

import com.rpandey.covid19tracker_india.data.model.covidIndia.ResourceData
import com.rpandey.covid19tracker_india.data.model.covidIndia.ResourceResponse
import com.rpandey.covid19tracker_india.database.entity.ResourcesEntity
import com.rpandey.covid19tracker_india.database.provider.CovidDatabase

class ResourceDataProcessor(covidDatabase: CovidDatabase) :
    ResponseProcessor<ResourceResponse>(covidDatabase) {

    override fun process(data: ResourceResponse) {
        val resources = data.resources
        if (resources.isEmpty())
            return

        val resourceEntities = mutableListOf<ResourcesEntity>()
        for (resource in resources) {
            try {
                resourceEntities.add(
                    ResourcesEntity(
                        resource.recordid.toInt(),
                        resource.category,
                        resource.district,
                        resource.state,
                        resource.contact,
                        getContact(resource),
                        resource.description,
                        resource.organisation
                    )
                )
            } catch (e: Exception) {}
        }

        if (resourceEntities.isNotEmpty())
            persistData(resourceEntities)
    }

    private fun getContact(resourceData: ResourceData): String {
        val rawContact = resourceData.phoneNumber?.trim() ?: ""
        return rawContact.replace("\n", "")
    }

    private fun persistData(resourceEntities: List<ResourcesEntity>) {
        covidDatabase.resourceDao().insert(resourceEntities)
    }
}