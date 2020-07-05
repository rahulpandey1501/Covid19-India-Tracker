package com.rpandey.covid19tracker_india.data.model

enum class IndianStates(val stateCode: String, val stateName: String) {
    AN("AN", "Andaman and Nicobar Islands"),
    AP("AP", "Andhra Pradesh"),
    AR("AR", "Arunachal Pradesh"),
    AS("AS", "Assam"),
    BR("BR", "Bihar"),
    CH("CH", "Chandigarh"),
    CT("CT", "Chhattisgarh"),
    DL("DL", "Delhi"),
    DN("DN", "Dadra and Nagar Haveli and Daman and Diu"),
    GA("GA", "Goa"),
    GJ("GJ", "Gujarat"),
    HP("HP", "Himachal Pradesh"),
    HR("HR", "Haryana"),
    JH("JH", "Jharkhand"),
    JK("JK", "Jammu and Kashmir"),
    KA("KA", "Karnataka"),
    KL("KL", "Kerala"),
    LA("LA", "Ladakh"),
    LD("LD", "Lakshadweep"),
    MH("MH", "Maharashtra"),
    ML("ML", "Meghalaya"),
    MN("MN", "Manipur"),
    MP("MP", "Madhya Pradesh"),
    MZ("MZ", "Mizoram"),
    NL("NL", "Nagaland"),
    OR("OR", "Odisha"),
    PB("PB", "Punjab"),
    PY("PY", "Puducherry"),
    RJ("RJ", "Rajasthan"),
    SK("SK", "Sikkim"),
    TG("TG", "Telangana"),
    TN("TN", "Tamil Nadu"),
    TR("TR", "Tripura"),
    UN("UN", "State Unassigned"),
    UP("UP", "Uttar Pradesh"),
    UT("UT", "Uttarakhand"),
    WB("WB", "West Bengal");

    companion object {
        fun from(stateCode: String): IndianStates {
            return values().find { it.stateCode == stateCode } ?: UN
        }
        fun fromName(stateName: String): IndianStates {
            return values().find { it.stateName == stateName } ?: UN
        }
    }
}