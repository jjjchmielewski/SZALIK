package com.szalik.logic.entertainment.enums

enum class FractionsDistribution(
    val players: Int,
    val city: Int,
    val bandits: Int,
    val indians: Int,
    val aliens: Int
) {
    SIX(6, 3, 2, 1, 0),
    SEVEN(7, 4, 2, 1, 0),
    EIGHT(8, 4, 2, 2, 0),
    NINE(9, 5, 2, 2, 0),
    TEN(10, 5, 3, 2, 0),
    ELEVEN(11, 5, 3, 3, 0),
    TWELVE(12, 5, 4, 3, 0),
    THIRTEEN(13, 6, 4, 3, 0),
    FOURTEEN(14, 6, 4, 4, 0),
    FIFTEEN(15, 7, 4, 4, 0),
    SIXTEEN(16, 7, 5, 4, 0),
    SEVENTEEN(17, 7, 5, 5, 0),
    EIGHTEEN(18, 6, 4, 5, 3),
    NINETEEN(19, 7, 4, 5, 3),
    TWENTY(20, 8, 4, 5, 3),
    TWENTY_ONE(21, 8, 4, 6, 3),
    TWENTY_TWO(22, 9, 4, 6, 3),
    TWENTY_THREE(23, 9, 4, 6, 4),
    TWENTY_FOUR(24, 10, 4, 6, 4),
    TWENTY_FIVE(25, 10, 5, 6, 4),
    TWENTY_SIX(26, 11, 5, 6, 4),
    TWENTY_SEVEN(27, 11, 5, 7, 4),
    TWENTY_EIGHT(28, 11, 6, 7, 4),
    TWENTY_NINE(29, 11, 6, 7, 5),
    THIRTY(30, 12, 6, 7, 5);

    companion object {
        fun fromPLayersNumber(number: Int) =
            FractionsDistribution.values().first { it.players == number }
    }
}