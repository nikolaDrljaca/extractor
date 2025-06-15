package com.drbrosdev.extractor.domain.model


// TODO Excessive - not necessary - rip this out
sealed class Embed {
    abstract val value: String

    data class Text(
        override val value: String,
    ) : Embed() {
        companion object {
            val DEFAULT = Text("")
        }
    }

    data class User(override val value: String) : Embed()

    data class Visual(override val value: String) : Embed()
}
