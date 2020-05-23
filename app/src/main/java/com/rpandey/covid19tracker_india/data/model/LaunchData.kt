package com.rpandey.covid19tracker_india.data.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
class LaunchData(
    @SerializedName("latestVersion")
    val latestVersion: Int,
    @SerializedName("downloadUrl")
    val downloadUrl: String,
    @SerializedName("apkDownloadUrl")
    var apkDownloadUrl: String?,
    @SerializedName("shareUrl")
    val shareUrl: String?,
    @SerializedName("marketUpload")
    val marketUpload: MarketUpload,
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
class MarketUpload(
    @SerializedName("marketName") val marketName: String,
    @SerializedName("downloadUrl") val downloadUrl: String
)

@Keep
class Config(
    @SerializedName("autoDownloadEnabled") var autoDownloadEnabled: Boolean = false,
    @SerializedName("districtInfoUrlPlaceholder") val districtInfoUrlPlaceholder: String?,
    @SerializedName("stateInfoUrlPlaceholder") val stateInfoUrlPlaceholder: String?,
    @SerializedName("analysisUrl") val analysisUrl: String?,
    @SerializedName("nearByEssentialsUrl") val nearByEssentialsUrl: String?
)