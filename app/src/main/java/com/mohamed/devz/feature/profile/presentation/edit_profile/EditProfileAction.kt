package com.mohamed.devz.feature.profile.presentation.edit_profile

sealed class EditProfileAction {
    object PickAvatar                            : EditProfileAction()
    data class FullNameChanged(val v: String)    : EditProfileAction()
    data class UsernameChanged(val v: String)    : EditProfileAction()
    data class BioChanged(val v: String)         : EditProfileAction()
    data class GithubChanged(val v: String)      : EditProfileAction()
    data class LinkedinChanged(val v: String)    : EditProfileAction()
    data class WebsiteChanged(val v: String)     : EditProfileAction()
    data class RemoveSkill(val skill: String)    : EditProfileAction()
    data class SkillInputChanged(val v: String)  : EditProfileAction()
    object ShowSkillInput                        : EditProfileAction()
    object AddSkill                              : EditProfileAction()
    object TogglePublicProfile                   : EditProfileAction()
    object ToggleDisplayEmail                    : EditProfileAction()
    object DeactivateAccount                     : EditProfileAction()
    object Save                                  : EditProfileAction()
}