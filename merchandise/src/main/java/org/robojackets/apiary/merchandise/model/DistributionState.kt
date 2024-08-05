package org.robojackets.apiary.merchandise.model

import org.robojackets.apiary.base.model.BasicUser
import org.robojackets.apiary.base.ui.nfc.BuzzCardTap

data class DistributionState(
    val tap: BuzzCardTap,
    val canDistribute: Boolean,
    val user: BasicUser,
    val name: String? = null,
    val pastDistribution: Boolean = false,
)
