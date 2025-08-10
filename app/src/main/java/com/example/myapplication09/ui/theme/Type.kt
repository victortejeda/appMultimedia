package com.example.myapplication09.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// ============================================================================
// TIPOGRAFÍA PERSONALIZADA PARA LA APLICACIÓN MULTIMEDIA
// ============================================================================

/**
 * Tipografía personalizada que mejora la legibilidad
 * y la apariencia moderna de la aplicación
 */
val Typography = Typography(
    // ===== TIPOGRAFÍA PARA DISPLAY (TÍTULOS PRINCIPALES) =====
    
    /**
     * Display Large - Para títulos principales muy grandes
     * Usado en la pantalla de bienvenida
     */
    displayLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp,
    ),
    
    /**
     * Display Medium - Para títulos principales medianos
     * Usado en encabezados de secciones importantes
     */
    displayMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 45.sp,
        lineHeight = 52.sp,
        letterSpacing = 0.sp,
    ),
    
    /**
     * Display Small - Para títulos principales pequeños
     * Usado en encabezados de secciones
     */
    displaySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 36.sp,
        lineHeight = 44.sp,
        letterSpacing = 0.sp,
    ),
    
    // ===== TIPOGRAFÍA PARA HEADLINES (TÍTULOS DE SECCIÓN) =====
    
    /**
     * Headline Large - Para títulos de sección grandes
     * Usado en nombres de secciones principales
     */
    headlineLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp,
    ),
    
    /**
     * Headline Medium - Para títulos de sección medianos
     * Usado en nombres de secciones
     */
    headlineMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp,
    ),
    
    /**
     * Headline Small - Para títulos de sección pequeños
     * Usado en nombres de subsecciones
     */
    headlineSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp,
    ),
    
    // ===== TIPOGRAFÍA PARA TITLES (TÍTULOS DE COMPONENTES) =====
    
    /**
     * Title Large - Para títulos de componentes grandes
     * Usado en nombres de botones principales
     */
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp,
    ),
    
    /**
     * Title Medium - Para títulos de componentes medianos
     * Usado en nombres de botones y elementos
     */
    titleMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp,
    ),
    
    /**
     * Title Small - Para títulos de componentes pequeños
     * Usado en etiquetas y nombres cortos
     */
    titleSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.1.sp,
    ),
    
    // ===== TIPOGRAFÍA PARA BODY (TEXTO PRINCIPAL) =====
    
    /**
     * Body Large - Para texto principal grande
     * Usado en descripciones y contenido importante
     */
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp,
    ),
    
    /**
     * Body Medium - Para texto principal mediano
     * Usado en descripciones y contenido regular
     */
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.25.sp,
    ),
    
    /**
     * Body Small - Para texto principal pequeño
     * Usado en descripciones cortas y notas
     */
    bodySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp,
    ),
    
    // ===== TIPOGRAFÍA PARA LABELS (ETIQUETAS Y BOTONES) =====
    
    /**
     * Label Large - Para etiquetas grandes
     * Usado en botones principales y navegación
     */
    labelLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.1.sp,
    ),
    
    /**
     * Label Medium - Para etiquetas medianas
     * Usado en botones secundarios y elementos
     */
    labelMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp,
    ),
    
    /**
     * Label Small - Para etiquetas pequeñas
     * Usado en etiquetas de formularios y notas
     */
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp,
    ),
)