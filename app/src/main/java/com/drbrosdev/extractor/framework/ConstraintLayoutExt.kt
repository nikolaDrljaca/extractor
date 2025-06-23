package com.drbrosdev.extractor.framework

import androidx.constraintlayout.compose.ConstrainScope
import androidx.constraintlayout.compose.Dimension


fun ConstrainScope.fillMaxWidth() {
    start.linkTo(parent.start)
    end.linkTo(parent.end)
    width = Dimension.fillToConstraints
}
