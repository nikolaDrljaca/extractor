package com.drbrosdev.extractor.domain.model


sealed class Embed {
    abstract val value: String

    data class Text(
        override val value: String,
//        val confidence: Float
    ) : Embed()

    data class User(override val value: String) : Embed()

    data class Visual(
        override val value: String,
//        val confidence: Float
    ) : Embed()

    companion object {
        val defaultTextEmbed = Text("")
        val defaultVisualEmbed = Visual("")
    }
}
