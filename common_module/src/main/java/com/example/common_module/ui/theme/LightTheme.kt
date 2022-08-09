package com.example.common_module.ui.theme

import androidx.compose.material.lightColors
import androidx.compose.ui.graphics.Color
import com.example.common_module.ui.theme.Red800
import com.example.common_module.ui.theme.pink_700
import com.example.common_module.ui.theme.pink_800
import com.example.common_module.ui.theme.pink_900

val LightColors = lightColors(
    primary = pink_700,
    primaryVariant = pink_800,
    onPrimary = Color.White,
    secondary = pink_700,
    secondaryVariant = pink_900,
    onSecondary = Color.White,
    error = Red800
)