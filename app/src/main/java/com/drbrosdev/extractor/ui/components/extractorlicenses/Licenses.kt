package com.drbrosdev.extractor.ui.components.extractorlicenses

data class License(
    val id: Int,
    val name: String,
    val link: String
)

val allLicenses = listOf(
    License(id = 1, name = "Compose Navigation Reimagined", link = "https://olshevski.github.io/compose-navigation-reimagined/"),
    License(id = 2, name = "Koin", link = "https://insert-koin.io"),
    License(id = 3, name = "Coil", link = "https://coil-kt.github.io/coil/"),
    License(id = 4, name = "Zoomable", link = "https://github.com/usuiat/Zoomable"),
    License(id = 5, name = "Arrow", link = "https://arrow-kt.io/"),
)
