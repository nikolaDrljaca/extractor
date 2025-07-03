package com.drbrosdev.extractor.framework.permission

enum class ReadPermissionAccess {
    FULL, // Full access on API level 33 or higher
    PARTIAL, // Partial (user selected) access on API level 34 or higher
    DENIED // Full access up to API level 32
}
