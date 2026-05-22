package com.mohamed.devz.feature.splash.presentation

sealed interface SplashAction {
    data object DecideWhereToGoAction : SplashAction
}