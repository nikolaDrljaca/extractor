package com.drbrosdev.extractor.domain.repository.payload

import com.drbrosdev.extractor.domain.model.MediaImageId

data class NewEmbed(
    val mediaImageId: MediaImageId,
    val value: String
)
