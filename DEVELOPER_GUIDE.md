# 🚀 Guía para Desarrolladores - Aplicación Multimedia

## 📋 Información Técnica

### Versiones de Dependencias
- **Android Gradle Plugin**: 8.2.0+
- **Kotlin**: 1.9.0+
- **Compile SDK**: 36 (Android 14)
- **Min SDK**: 34 (Android 14)
- **Target SDK**: 36 (Android 14)
- **Jetpack Compose BOM**: 2024.01.00

### Arquitectura del Proyecto

#### Patrón MVVM (Model-View-ViewModel)
```
┌─────────────────┐    ┌──────────────────┐    ┌─────────────────┐
│     View        │    │   ViewModel      │    │     Model       │
│  (Composables)  │◄──►│  (AudioViewModel)│◄──►│  (Media APIs)   │
└─────────────────┘    └──────────────────┘    └─────────────────┘
```

#### Componentes Principales

1. **MainActivity**: Punto de entrada principal
2. **AudioViewModel**: Maneja toda la lógica de negocio
3. **Composables**: UI declarativa con Jetpack Compose
4. **Navigation**: Navegación entre pantallas

## 🔧 Configuración del Entorno

### Prerrequisitos
- Android Studio Hedgehog (2023.1.1) o superior
- JDK 17 o superior
- Android SDK API 34+
- Dispositivo Android o emulador con API 34+

### Configuración Inicial
1. Clona el repositorio
2. Abre el proyecto en Android Studio
3. Sincroniza las dependencias de Gradle
4. Ejecuta la aplicación

## 📱 Estructura de la UI

### Pantallas
1. **WelcomeScreen**: Pantalla de bienvenida con información de desarrolladores
2. **AudioProjectScreen**: Pantalla principal con toda la funcionalidad multimedia

### Componentes Reutilizables
- `AudioSection`: Sección con título, descripción y contenido
- `AudioButton`: Botón personalizado para controles de audio
- `AuthorCard`: Tarjeta para mostrar información de desarrolladores

## 🎵 Funcionalidades de Audio

### SoundPool
- **Propósito**: Reproducción de efectos de sonido cortos
- **Configuración**: Máximo 2 streams simultáneos
- **Uso**: Tonos de notificación y efectos

### MediaPlayer
- **Propósito**: Reproducción de música larga
- **Formato**: MP3 desde recursos raw
- **Control**: Play/Stop con estado observable

### MediaRecorder
- **Propósito**: Grabación de audio desde micrófono
- **Formato**: 3GP con codificador AMR
- **Almacenamiento**: Cache temporal + librería multimedia

## 🔐 Manejo de Permisos

### Permisos Requeridos
```xml
<uses-permission android:name="android.permission.RECORD_AUDIO" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
```

### Solicitud de Permisos
- Implementado con `ActivityResultLauncher`
- Verificación en tiempo de ejecución
- Manejo de casos de denegación

## 🎨 Sistema de Temas

### Colores
- **Primario**: Púrpura moderno (#6750A4)
- **Secundario**: Turquesa vibrante (#03DAC5)
- **Error**: Rojo moderno (#B3261E)
- **Superficie**: Blanco puro (#FFFBFE)

### Tipografía
- **Display**: Para títulos principales
- **Headline**: Para títulos de sección
- **Title**: Para títulos de componentes
- **Body**: Para texto principal
- **Label**: Para etiquetas y botones

## 📁 Estructura de Archivos

```
app/src/main/
├── java/com/example/myapplication09/
│   ├── MainActivity.kt              # Actividad principal
│   └── ui/theme/
│       ├── Color.kt                 # Paleta de colores
│       ├── Theme.kt                 # Configuración de temas
│       └── Type.kt                  # Tipografía personalizada
├── res/
│   ├── raw/                         # Archivos de audio
│   │   ├── tono_corto.mp3          # Tono corto
│   │   ├── otro_tono.mp3           # Segundo tono
│   │   └── cancion_larga.mp3       # Música de fondo
│   ├── values/
│   │   ├── colors.xml               # Colores del tema
│   │   └── strings.xml              # Cadenas de texto
│   └── AndroidManifest.xml          # Configuración de la app
├── build.gradle.kts                 # Configuración de build
└── proguard-rules.pro               # Reglas de ofuscación
```

## 🚀 Mejoras Implementadas

### Código
- ✅ Estructura limpia y organizada
- ✅ Comentarios de junior en español
- ✅ Separación clara de responsabilidades
- ✅ Componentes reutilizables
- ✅ Manejo correcto de recursos

### UI/UX
- ✅ Diseño moderno con Material Design 3
- ✅ Paleta de colores personalizada
- ✅ Tipografía mejorada y legible
- ✅ Componentes organizados en secciones
- ✅ Navegación intuitiva
- ✅ Estados visuales claros

### Funcionalidad
- ✅ Reproducción de audio completa
- ✅ Grabación de audio funcional
- ✅ Almacenamiento en librería multimedia
- ✅ Manejo de permisos robusto
- ✅ Log de actividad en tiempo real

## 🐛 Solución de Problemas Comunes

### Error de Compilación
```bash
# Limpia y reconstruye el proyecto
./gradlew clean
./gradlew build
```

### Error de Permisos
- Verifica que `RECORD_AUDIO` esté en el manifest
- Asegúrate de solicitar permisos en tiempo de ejecución
- Prueba en un dispositivo físico (los emuladores pueden tener problemas)

### Error de Audio
- Verifica que los archivos estén en `res/raw/`
- Asegúrate de que los formatos sean compatibles
- Verifica que el dispositivo tenga altavoces/auriculares

### Error de Grabación
- Verifica permisos de micrófono
- Asegúrate de tener espacio de almacenamiento
- Prueba en diferentes dispositivos

## 🔄 Flujo de Desarrollo

### 1. Desarrollo Local
```bash
git clone [REPOSITORIO]
cd appMultimedia
# Abre en Android Studio
# Desarrolla y prueba localmente
```

### 2. Testing
- Ejecuta en emulador
- Prueba en dispositivo físico
- Verifica diferentes tamaños de pantalla
- Prueba orientación landscape/portrait

### 3. Commit y Push
```bash
git add .
git commit -m "Descripción del cambio"
git push origin main
```

## 📚 Recursos Adicionales

### Documentación Oficial
- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Material Design 3](https://m3.material.io/)
- [Android Media APIs](https://developer.android.com/guide/topics/media)

### Herramientas Útiles
- [Android Studio](https://developer.android.com/studio)
- [Layout Inspector](https://developer.android.com/studio/debug/layout-inspector)
- [Device File Explorer](https://developer.android.com/studio/debug/device-file-explorer)

## 🤝 Contribución

### Estándares de Código
- Usa nombres descriptivos en español
- Agrega comentarios explicativos
- Sigue el patrón MVVM
- Mantén la consistencia en el estilo

### Proceso de Review
1. Crea una rama para tu feature
2. Implementa los cambios
3. Agrega tests si es necesario
4. Crea un Pull Request
5. Espera la revisión del equipo

## 📞 Soporte

### Contacto del Equipo
- **Henry Castro**: 1-21-4112
- **Lissette Rodríguez**: 1-19-3824
- **Miguel Berroa**: 2-16-3694

### Canales de Comunicación
- Issues del repositorio
- Pull Requests
- Comentarios en el código

---

**Nota**: Esta guía se actualiza regularmente. Mantente al día con los últimos cambios del proyecto.
