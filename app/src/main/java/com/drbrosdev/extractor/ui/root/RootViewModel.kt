package com.drbrosdev.extractor.ui.root

import androidx.lifecycle.ViewModel
import com.drbrosdev.extractor.data.ExtractorDataStore
import com.drbrosdev.extractor.framework.permission.PermissionService

class RootViewModel(
    private val datastore: ExtractorDataStore,
    private val permissionService: PermissionService
) : ViewModel() {

    fun isOnboardingFinished() = datastore.isOnboardingFinished

    fun getPermissionAccessStatus() = permissionService.getReadPermissionAccessStatus()
}