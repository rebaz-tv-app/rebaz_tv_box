package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey val channelId: String,
    val addedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "custom_channels")
data class CustomChannelEntity(
    @PrimaryKey val id: String,
    val number: Int,
    val name: String,
    val category: String,
    val streamUrl: String,
    val logoBadge: String = "",
    val subtitle: String = "پەخشی دەرەکی"
)
