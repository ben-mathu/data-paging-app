package com.benatt.datapagingapp.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * @author ben-mathu
 * @since 2/23/25
 */
@Entity(tableName = "businesses")
class Business(
    @PrimaryKey(autoGenerate = true)
    @JsonProperty("id")
    val id: Int,
    @JsonProperty("business_name")
    val businessName: String,
    @JsonProperty("address_line_1")
    val addressLine1: String,
    @JsonProperty("address_line_2")
    val addressLine2: String,
    @JsonProperty("city")
    val city: String,
    @JsonProperty("country")
    val country: String,
)