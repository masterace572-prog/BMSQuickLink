package com.bms.quicklink.data

data class SwitchState(
    val chargeOn: Boolean = false,
    val chargePending: Boolean = false,

    val dischargeOn: Boolean = false,
    val dischargePending: Boolean = false,

    val balanceOn: Boolean = false,
    val balancePending: Boolean = false,

    val heatingOn: Boolean = false,
    val heatingPending: Boolean = false,
    val heatingWritable: Boolean = true // True if writable toggle, false if notify-only indicator
)
