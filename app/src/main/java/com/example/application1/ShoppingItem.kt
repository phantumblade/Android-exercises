package com.example.application1

// I valori di default (= "") sono obbligatori per Firebase
data class ShoppingItem(
    val id: String? = null,
    val text: String = "",
    val date: String = ""
)
