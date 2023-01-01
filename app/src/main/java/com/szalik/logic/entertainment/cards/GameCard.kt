package com.szalik.logic.entertainment.cards

import com.szalik.logic.entertainment.enums.Fraction
import com.szalik.logic.entertainment.enums.Role

data class GameCard(
    val role: Role? = null,
    var hasTotem: Boolean = false,
    var isJailed: Boolean = false,
    var isBlackmailed: Boolean = false,
    var isProtected: Boolean = false,
    var isDrunk: Boolean = false,
    var isSeduced: Boolean = false,
    var isPoisoned: Boolean = false,
    var isPlaying: Boolean = false,
    var actionsLeftCounter: Int? = role?.actionsCount
)