package com.bms.quicklink.data

enum class SwitchType(val title: String, val prompt: String) {
    CHARGE("Charge MOSFET", "Change Charging Path state?"),
    DISCHARGE("Discharge MOSFET", "Change Battery Output state?"),
    BALANCE("Auto Balance", "Change Auto Balance state?"),
    HEATING("Heating", "Change Heating state?")
}
