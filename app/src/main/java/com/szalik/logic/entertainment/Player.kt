package com.szalik.logic.entertainment

import com.szalik.logic.entertainment.cards.GameCard

data class Player(
    val name: String = "",
    val id: String = "",
    var eliminated: Boolean = false,
    var card: GameCard? = null
)
