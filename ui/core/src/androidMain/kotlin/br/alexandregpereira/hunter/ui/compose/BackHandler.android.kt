package br.alexandregpereira.hunter.ui.compose

import androidx.compose.runtime.Composable

@Composable
actual fun BackPlatformHandler(enabled: Boolean, onBack: () -> Unit) {
    androidx.activity.compose.BackHandler(enabled = enabled, onBack = onBack)
}