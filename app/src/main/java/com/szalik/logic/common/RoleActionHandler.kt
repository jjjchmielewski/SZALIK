package com.szalik.logic.common

import com.szalik.logic.entertainment.GameFlow
import com.szalik.logic.entertainment.enums.Fraction
import com.szalik.logic.entertainment.enums.Role

class RoleActionHandler {
    companion object {
        fun handle(): String {
            return when (if (GameFlow.actionTakeover != null) GameFlow.actionTakeover else GameFlow.listOfPlayers.find { it.id == GameFlow.currentPlayerId }?.card?.role) {
                Role.COQUETTE -> {
                    if (GameFlow.showChoiceList) {
                        "Wybierz osobę z którą chcesz się zapoznać"
                    } else {
                        GameFlow.showIdentity = true
                        "Tożsamość poznanej osoby:"
                    }
                }
                Role.SEDUCER -> {
                    if (GameFlow.showChoiceList) {
                        "Wybierz osobę którą chcesz uwieść"
                    } else {
                        GameFlow.showConfirmButton = true
                        "Wybrana osoba została uwiedziona"
                    }
                }
                Role.SHERIFF -> {
                    if (GameFlow.showChoiceList) {
                        "Wybierz kogo chcesz zaaresztować"
                    } else {
                        GameFlow.showConfirmButton = true
                        if (GameFlow.thisPlayerId == GameFlow.playerWithTotemId) {
                            "Udało ci się przejąć posążek!"
                        } else {
                            "Zaaresztowana osoba nie miała posążka"
                        }
                    }
                }
                Role.PRIEST -> {
                    if (GameFlow.showChoiceList) {
                        "Wybierz kogo chcesz wyspowiadać"
                    } else {
                        GameFlow.showIdentity = true
                        "Frakcja wyspowiadanej osoby to:"
                    }
                }
                Role.EXECUTIONER -> {
                    if (GameFlow.showChoiceList) {
                        "Wybierz kogo chcesz zabić"
                    } else {
                        GameFlow.showConfirmButton = true
                        if (GameFlow.thisPlayerId == GameFlow.playerWithTotemId) {
                            "Zabita osoba miała przy sobie posążek, teraz jest twój!"
                        } else {
                            "Wybrałeś osobę do zabicia"
                        }
                    }
                }
                Role.DRUNKARD -> {
                    if (GameFlow.showChoiceList) {
                        "Wybierz osobę z którą pójdziesz się napić"
                    } else {
                        GameFlow.showConfirmButton = true
                        "Upiłeś wybraną osobę"
                    }
                }
                Role.BODYGUARD -> {
                    if (GameFlow.showChoiceList) {
                        "Wybierz osobę, którą będziesz chronić tej nocy"
                    } else {
                        GameFlow.showConfirmButton = true
                        "Chronisz wybraną osobę"
                    }
                }
                Role.TAXMAN -> {
                    "Osoba posiadająca posążek to:"
                }
                Role.WARLORD -> {
                    if (GameFlow.showChoiceList) {
                        if (GameFlow.listOfPlayers.find { it.id == GameFlow.playerWithTotemId }?.card?.role?.fraction == Fraction.BANDITS) {
                            "Naradź się z innymi Bandytami i wskaż kto ma przechować posążek tej nocy"
                        } else {
                            "Naradź się z innymi bandytami i wskaż kogo przeszukać tej nocy"
                        }
                    } else {
                        GameFlow.showConfirmButton = true
                        if (GameFlow.thisPlayerId == GameFlow.playerWithTotemId) {
                            "Otrzymujesz posążek!"
                        } else if (GameFlow.listOfPlayers.find { it.id == GameFlow.playerWithTotemId }?.card?.role?.fraction == Fraction.BANDITS) {
                            "Posążek został przekazany"
                        } else {
                            "Wybrana osoba nie miała przy sobie posążka"
                        }
                    }
                }
                Role.THIEF -> {
                    if (GameFlow.showChoiceList) {
                        "Kogo chcesz okraść?"
                    } else {
                        GameFlow.showConfirmButton = true
                        if (GameFlow.thisPlayerId == GameFlow.playerWithTotemId) {
                            "Udaje ci się ukraść posążek!"
                        } else {
                            "Wybrana osoba nie miała przy sobie posążka"
                        }
                    }
                }
                Role.GAMBLER -> {
                    if (GameFlow.showChoiceList) {
                        "Wybierz osobę którą chcesz ograć w karty"
                    } else {
                        GameFlow.showConfirmButton = true
                        if (GameFlow.thisPlayerId == GameFlow.playerWithTotemId) {
                            "Udaje ci się wygrać posążek!"
                        } else {
                            "Wybrana osoba nie miała przy sobie posążka"
                        }
                    }
                }
                Role.BLACKMAILER -> {
                    if (GameFlow.showChoiceList) {
                        "Wybierz osobę którą chcesz zaszantażować"
                    } else {
                        GameFlow.showConfirmButton = true
                        "Wybrana osoba będzie się ciebie słuchać"
                    }
                }
                Role.CHIEF -> {
                    if (GameFlow.showChoiceList) {
                        if (GameFlow.listOfPlayers.find { it.id == GameFlow.playerWithTotemId }?.card?.role?.fraction == Fraction.INDIANS && GameFlow.indiansKillCounter == 0) {
                            "Naradź się z innymi Indianinami i wskaż kto ma przechować tej nocy posążek"
                        } else {
                            "Naradź się z innymi Indianinami i wskaż bladą twarz do zabicia"
                        }
                    } else {
                        GameFlow.showConfirmButton = true
                        if (GameFlow.listOfPlayers.find { it.id == GameFlow.playerWithTotemId }?.card?.role?.fraction == Fraction.INDIANS && GameFlow.indiansKillCounter == 2) {
                            "Posążek przekazany"
                        } else if (GameFlow.listOfPlayers.find { it.id == GameFlow.playerWithTotemId }?.card?.role?.fraction != Fraction.INDIANS && GameFlow.indiansKillCounter == 1) {
                            "Wybrałeś osobę do zabicia"
                        } else if (GameFlow.listOfPlayers.find { it.id == GameFlow.playerWithTotemId }?.card?.role?.fraction == Fraction.INDIANS && GameFlow.indiansKillCounter == 1) {
                            "Zabita blada twarz miała przy sobie posążek, przejmujesz go!"
                        } else {
                            "Wybrałeś osobę do zabicia"
                        }
                    }
                }
                Role.WARRIOR -> {
                    if (GameFlow.showChoiceList) {
                        "Wybierz bladą twarz do zabicia"
                    } else {
                        GameFlow.showConfirmButton = true
                        if (GameFlow.thisPlayerId == GameFlow.playerWithTotemId) {
                            "Zabijasz bladą twarz i przejmujesz posążek!"
                        } else {
                            "Wybrana osoba została zabita"
                        }
                    }
                }
                Role.BINOCULARS_EYE -> {
                    "Osoba posiadająca posążek to:"
                }
                Role.LONELY_COYOTE -> {
                    if (GameFlow.showChoiceList) {
                        "Wybierz bladą twarz do zabicia"
                    } else {
                        GameFlow.showConfirmButton = true
                        if (GameFlow.thisPlayerId == GameFlow.playerWithTotemId) {
                            "Zabijasz bladą twarz i przejmujesz posążek!"
                        } else {
                            "Wybrana osoba została zabita"
                        }
                    }
                }
                Role.BURNING_RAGE -> {
                    if (GameFlow.showChoiceList) {
                        "Wybierz bladą twarz do zabicia"
                    } else {
                        GameFlow.showConfirmButton = true
                        if (GameFlow.thisPlayerId == GameFlow.playerWithTotemId) {
                            "Zabijasz bladą twarz i przejmujesz posążek!"
                        } else {
                            "Wybrana osoba została zabita"
                        }
                    }
                }
                Role.SHAMAN -> {
                    if (GameFlow.showChoiceList) {
                        "Wybierz osobę której kartę chcesz poznać"
                    } else {
                        GameFlow.showIdentity = true
                        "Tożsamość wybranej osoby to:"
                    }
                }
                Role.GREAT_ALIEN -> {
                    if (GameFlow.showChoiceList) {
                        if (GameFlow.listOfPlayers.find { it.id == GameFlow.playerWithTotemId }?.card?.role?.fraction == Fraction.ALIENS) {
                            "Nadajecie ${GameFlow.aliensSignalCounter} sygnał na swoją planetę! Naradź się z innymi Kosmitami i wskaż kto ma przechować tej nocy posążek"
                        } else {
                            "Naradź się z innymi Kosmitami i wskaż ziemianina do przeszukania"
                        }
                    } else {
                        GameFlow.showConfirmButton = true
                        if (GameFlow.thisPlayerId == GameFlow.playerWithTotemId) {
                            "Udaje ci się zdobyć posążek! Nadajecie ${GameFlow.aliensSignalCounter} sygnał na swoją planetę!"
                        } else {
                            "Wybrany ziemianin nie miał przy sobie posążka"
                        }
                    }
                }
                Role.PURPLE_SUCTION -> {
                    if (GameFlow.showChoiceList) {
                        "Wskaż ziemianina do przeszukania"
                    } else {
                        GameFlow.showConfirmButton = true
                        if (GameFlow.thisPlayerId == GameFlow.playerWithTotemId) {
                            "Udaje ci się znaleźć posążek!"
                        } else {
                            "Wybrany ziemianin nie miał przy sobie posążka"
                        }
                    }
                }
                Role.GREEN_TENTACLE -> {
                    if (GameFlow.showChoiceList) {
                        "Wskaż ziemianina do zabicia"
                    } else {
                        GameFlow.showConfirmButton = true
                        if (GameFlow.thisPlayerId == GameFlow.playerWithTotemId) {
                            "Zabijasz ziemianina i przejmujesz posążek!"
                        } else {
                            "Wybrany ziemianin został zabity"
                        }
                    }
                }
                Role.MIND_EATER -> {
                    if (GameFlow.showChoiceList) {
                        "Wybierz  ziemianina którego tożsamość chcesz poznać"
                    } else {
                        GameFlow.showIdentity = true
                        "Tożsamość wybranego ziemianina to"
                    }
                }
                else -> {
                    GameFlow.showChoiceList = false
                    ""
                }
            }
        }
    }
}