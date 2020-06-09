package com.rpandey.covid19tracker_india.data.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
class LaunchData(
    @SerializedName("latestVersion")
    val latestVersion: Int,
    @SerializedName("shareUrl")
    val shareUrl: String?,
    @SerializedName("forceUpdate")
    val forceUpdate: ForceUpdate,
    @SerializedName("message")
    val message: Message,
    @SerializedName("config")
    val config: Config?
)

@Keep
class ForceUpdate(
    @SerializedName("minVersion") val minVersion: Int
)

@Keep
class Message(
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String
)

@Keep
class Config(
    @SerializedName("districtInfoUrlPlaceholder") val districtInfoUrlPlaceholder: String?,
    @SerializedName("stateInfoUrlPlaceholder") val stateInfoUrlPlaceholder: String?,
    @SerializedName("analysisUrl") val analysisUrl: String?,
    @SerializedName("nearByEssentialsUrl") val nearByEssentialsUrl: String?
)