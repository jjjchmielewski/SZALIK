package com.szalik.logic.entertainment.enums

enum class Role(
    val polishName: String,
    val description: String,
    val actionsCount: Int,
    val fraction: Fraction
) {
    JUDGE(
        "Sędzia",
        "Raz w ciągu gry może ujawnić swoją kartę i samodzielnie ogłosić wynik pojedynku. Decyzja sędziego przewyższa zdolność Rewolwerowca",
        1,
        Fraction.CITY
    ),
    GOOD_GUNSLINGER(
        "Dobry rewolwerowiec",
        "Wygrywa każdy pojedynek niezależnie od wyniku, o ile nie walczy ze Złym Rewolwerowcem",
        999,
        Fraction.CITY
    ),
    CITIZEN(
        "Miastowy",
        "Nie ma specjalnych zdolności",
        999,
        Fraction.CITY
    ),
    INSURANCE_AGENT(
        "Agent ubezpieczeniowy",
        "W dowolnym momencie gry może ujawnić swoją rolę",
        1,
        Fraction.CITY
    ),
    EXECUTIONER(
        "Kat",
        "Może raz w ciągu gry zabić wybraną osobę",
        1,
        Fraction.CITY
    ),
    DRUNKARD(
        "Opój",
        "Opój może dwa razy w ciągu gry wybrać osobę z którą chce pójść się napić. Ta osoba nie będzie się budzić tej nocy, jednakże można ją zabić, przeszukać czy w jakiś inny sposób na nią oddziaływać",
        2,
        Fraction.CITY
    ),
    BODYGUARD(
        "Ochroniarz",
        "Ochroniarz wybiera co noc osobę którą chroni. Ta osoba nie może tej nocy zostać zabita. Ochroniarz musi każdej nocy ochraniać inną osobę. Gdy ochroniarz zostanie zabity w nocy, ochrona przestaje działać",
        999,
        Fraction.CITY
    ),
    TAXMAN(
        "Poborca podatkowy",
        "Raz w trakcie gry może na podstawie zeznań majątkowych dowiedzieć się kto ma posążek",
        1,
        Fraction.CITY
    ),
    PRIEST(
        "Pastor",
        "Co noc (również zerowej nocy) wybiera osobę którą chce wyspowiadać i dowiaduje się, z jakiej frakcji jest ta osoba. Osoba spowiadana nie wie, że została wyspowiadana",
        999,
        Fraction.CITY
    ),
    SHERIFF(
        "Szeryf",
        "Szeryf, co noc (również zerowej nocy) zamyka w więzieniu jedną osobę. Jeżeli ta osoba miała posążek, przejmuje go szeryf. Osoba aresztowana staje się nieaktywna przez jedną noc tj. nie budzi się i nie może zostać zabita",
        999,
        Fraction.CITY
    ),
    SEDUCER(
        "Uwodziciel",
        "Uwodziciel dzięki swojemu urokowi osobistemu potrafi sprawić, że osoba którą sobie wybierze zerowej nocy przyzna mu zawsze rację i nie wystąpi w żaden sposób przeciwko niemu",
        999,
        Fraction.CITY
    ),
    COQUETTE(
        "Kokietka",
        "Zerowej nocy kokietka wybiera jednego gracza i zapoznaje się z nim. Poznaje jego kartę, a wybrany gracz dowiaduje się kto jest kokietką",
        999,
        Fraction.CITY
    ),
    THIEF(
        "Złodziej",
        "Złodziej raz w ciągu gry może okraść wybraną przez siebie osobę. Jeśli wybrana osoba ma posążek, przejmuje go.",
        1,
        Fraction.BANDITS
    ),
    WARLORD(
        "Herszt",
        "Herszt na początku gry posiada posążek. Podejmuje ostateczne decyzje w nocy, dotyczące kogo bandyci okradają, zabijają i u kogo ukrywają łup",
        999,
        Fraction.BANDITS
    ),
    GAMBLER(
        "Szuler",
        "Szuler może raz w trakcie gry wybrać osobę z którą będzie grał. Ta osoba nie będzie się budzić tej nocy. Ponadto, jeśli ta osoba posiada posążek, przejmuje go szuler",
        1,
        Fraction.BANDITS
    ),
    BAD_GUNSLINGER(
        "Zły rewolwerowiec",
        "Zwycięża pojedynki z prawie wszystkimi, niezależnie od wyników głosowania. W pojedynku może go pokonać jedynie Dobry Rewolwerowiec",
        999,
        Fraction.BANDITS
    ),
    BANDIT(
        "Bandyta",
        "Nie ma specjalnych zdolności",
        999,
        Fraction.BANDITS
    ),
    BLACKMAILER(
        "Szantażysta",
        "Szantażysta dzięki swojemu uzbrojeniu potrafi sprawić, że osoba którą sobie wybierze zerowej nocy przyzna mu zawsze rację i nie wystąpi przeciwko niemu w żaden sposób",
        999,
        Fraction.BANDITS
    ),
    WARRIOR(
        "Wojownik",
        "Raz w ciągu gry może dodatkowo zabić jedną osobę",
        1,
        Fraction.INDIANS
    ),
    INDIAN(
        "Indianin",
        "Nie ma specjalnych zdolności",
        999,
        Fraction.INDIANS
    ),
    BINOCULARS_EYE(
        "Lornecie oko",
        "Lornecie Oko może raz w trakcie gry dowiedzieć się, gdzie znajduje się posążek",
        1,
        Fraction.INDIANS
    ),
    CHIEF(
        "Wódz",
        "Podejmuje ostateczne decyzje dotyczące zabicia kogoś w nocy przez Indian i ukrycia posążka",
        999,
        Fraction.INDIANS
    ),
    LONELY_COYOTE(
        "Samotny kojot",
        "Jeśli Samotny Kojot jest jedynym aktywnym Indianinem, zabija dodatkowo jedną osobę",
        999,
        Fraction.INDIANS
    ),
    BURNING_RAGE(
        "Płonący szał",
        "Jeżeli danej nocy Indianie przejęli posążek, Płonący Szał zabija dodatkowo jedną osobę",
        999,
        Fraction.INDIANS
    ),
    SHAMAN(
        "Szaman",
        "Szaman raz w trakcie gry może wpaść w trans i poznać kartę jednej osoby",
        1,
        Fraction.INDIANS
    ),
    PURPLE_SUCTION(
        "Purpurowa przyssawka",
        "Purpurowa Przyssawka może raz w trakcie gry próbować ukraść posążek. Wybiera osobę i o ile ta osoba ma posążek, przejmuje go",
        1,
        Fraction.ALIENS
    ),
    ALIEN(
        "Kosmita",
        "Nie ma specjalnych zdolności",
        999,
        Fraction.ALIENS
    ),
    GREEN_TENTACLE(
        "Zielona macka",
        "Zielona Macka ma prawo raz w ciągu gry zabić wybraną osobę",
        1,
        Fraction.ALIENS
    ),
    MIND_EATER(
        "Pożeracz umysłów",
        "Każdej zwykłej nocy Pożeracz Umysłów może obejrzeć kartę wybranego gracza",
        999,
        Fraction.ALIENS
    ),
    GREAT_ALIEN(
        "Wielki kosmita",
        "Wielki kosmita w nocy ostatecznie podejmuje decyzje dotyczące wszystkich kosmitów, takie jak kogo okraść i u kogo ukryć posążek",
        999,
        Fraction.ALIENS
    )
}
