package com.example.data.model

data class Channel(
    val id: String,
    val number: Int,
    val name: String,
    val category: String,
    val streamUrl: String,
    val logoBadge: String = "",
    val subtitle: String = "پەخشی ڕاستەوخۆ بە کوالێتی بەرز",
    val isFavorite: Boolean = false,
    val isHd: Boolean = true
)

data class Category(
    val id: String,
    val nameKurdish: String,
    val iconName: String = "tv"
)
