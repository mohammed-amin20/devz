package com.mohamed.devz.feature.profile.presentation.edit_profile

sealed class EditProfileAction {
    data class PickImage(val imageBytes: ByteArray) : EditProfileAction()
    data class FullNameChanged(val v: String) : EditProfileAction()
    data class UsernameChanged(val v: String) : EditProfileAction()
    data class BioChanged(val v: String) : EditProfileAction()
    data class GithubChanged(val v: String) : EditProfileAction()
    data class LinkedinChanged(val v: String) : EditProfileAction()
    data class WebsiteChanged(val v: String) : EditProfileAction()
    data class RemoveSkill(val skill: String) : EditProfileAction()
    data class SkillInputChanged(val v: String) : EditProfileAction()
    data object ShowSkillInput : EditProfileAction()
    data object AddSkill : EditProfileAction()
    data object TogglePublicProfile : EditProfileAction()
    data object ToggleDisplayEmail : EditProfileAction()
    data object DeactivateAccount : EditProfileAction()
    data class Save(val onSave: () -> Unit) : EditProfileAction()
}