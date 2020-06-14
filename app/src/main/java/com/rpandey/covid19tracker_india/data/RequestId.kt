package com.rpandey.covid19tracker_india.data

// possible maintain sync order high to low priority
enum class RequestId {
    OVERALL_DATA,
    ZONE_DATA,
    LAUNCH_DATA,
    NEWS_DATA,
    TIME_SERIES,
    RESOURCE_DATA,
    FILE_DOWNLOAD
}