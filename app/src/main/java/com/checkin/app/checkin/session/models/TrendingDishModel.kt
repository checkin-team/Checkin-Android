package com.checkin.app.checkin.session.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class TrendingDishModel(
        val pk: Long,
        val name: String,
        val customizations: List<Int>?,
        @JsonProperty("types") val typeNames: List<String>,
        @JsonProperty("costs") val typeCosts: List<Double>,
        val description: String?,
        val tags: List<String>?,
        @JsonProperty("is_available") val isAvailable: Boolean,
        @JsonProperty("is_vegetarian") val isVegetarian: Boolean,
        val image: String?
) {
    var availableMeals: List<AVAILABLE_MEAL>? = null

    val isComplexItem: Boolean
        get() = hasCustomizations || typeNames.size > 1

    @JsonProperty("available_meals")
    fun setAvailableMeals(availableMeals: Array<String>) {
        this.availableMeals = availableMeals.map { AVAILABLE_MEAL.getByTag(it) }
    }

    private val hasCustomizations: Boolean
        get() = customizations?.isNotEmpty() ?: false
}

enum class AVAILABLE_MEAL private constructor(val tag: String) {
    BREAKFAST("brkfst"), LUNCH("lunch"), DINNER("dinner"),
    NIGHTLIFE("nhtlfe");

    companion object {
        fun getByTag(tag: String): AVAILABLE_MEAL {
            for (meal in values()) {
                if (meal.tag == tag)
                    return meal
            }
            return BREAKFAST
        }
    }
}
