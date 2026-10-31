package com.ovasta.sellers.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MilestonesProgress(
    @SerialName("orders_today") val ordersToday: Int = 0,
    @SerialName("current_milestone_percentage") val currentMilestonePercentage: Double? = null,
    @SerialName("next_milestone_threshold") val nextMilestoneThreshold: Int? = null,
    @SerialName("next_milestone_percentage") val nextMilestonePercentage: Double? = null,
    @SerialName("orders_remaining_to_next_milestone") val ordersRemainingToNextMilestone: Int? = null,
    @SerialName("earned_today") @Serializable(with = FlexibleDoubleSerializer::class) val earnedToday: Double = 0.0,
    @SerialName("earned_this_week") @Serializable(with = FlexibleDoubleSerializer::class) val earnedThisWeek: Double = 0.0,
    @SerialName("earned_this_month") @Serializable(with = FlexibleDoubleSerializer::class) val earnedThisMonth: Double = 0.0,
)
