package com.mohamed.devz.feature.company.presentation.edit_company_profile

sealed class EditCompanyProfileAction {
    data class PickImage(val imageBytes: ByteArray) : EditCompanyProfileAction()
    data class CompanyNameChanged(val v: String) : EditCompanyProfileAction()
    data class WebsiteChanged(val v: String) : EditCompanyProfileAction()
    data class DescriptionChanged(val v: String) : EditCompanyProfileAction()
    data class BioChanged(val v: String) : EditCompanyProfileAction()
    data class LocationChanged(val v: String) : EditCompanyProfileAction()
    data class IndustryChanged(val v: String) : EditCompanyProfileAction()
    data class TwitterChanged(val v: String) : EditCompanyProfileAction()
    data object ClearError : EditCompanyProfileAction()
    data class Save(val onSave: () -> Unit) : EditCompanyProfileAction()
}
