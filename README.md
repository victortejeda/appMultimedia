# 🎵 Aplicación Multimedia Android

## 📱 Descripción
Aplicación Android desarrollada en Kotlin que demuestra el manejo completo de audio multimedia, incluyendo reproducción de sonidos, grabación de audio y gestión de archivos multimedia.

## ✨ Características Principales

### 🎶 Reproducción de Audio
- **SoundPool**: Reproducción de tonos cortos y efectos de sonido
- **MediaPlayer**: Reproducción de canciones largas con control de estado
- **Grabación de Audio**: Captura de audio desde el micrófono del dispositivo

### 🔧 Funcionalidades Técnicas
- Manejo de permisos de audio en tiempo real
- Grabación en formato 3GP
- Almacenamiento automático en la librería multimedia del dispositivo
- Reproducción de grabaciones realizadas
- Interfaz moderna con Jetpack Compose

## 🛠️ Tecnologías Utilizadas

- **Kotlin**: Lenguaje principal de desarrollo
- **Jetpack Compose**: UI moderna declarativa
- **ViewModel**: Arquitectura MVVM para gestión de estado
- **Coroutines**: Programación asíncrona
- **MediaRecorder**: API nativa para grabación de audio
- **MediaPlayer**: Reproducción de archivos multimedia
- **SoundPool**: Reproducción de efectos de sonido

## 📁 Estructura del Proyecto

```
app/
├── src/main/
│   ├── java/com/example/myapplication09/
│   │   ├── MainActivity.kt          # Actividad principal
│   │   └── ui/theme/                # Temas y estilos
│   ├── res/
│   │   ├── raw/                     # Archivos de audio
│   │   │   ├── tono_corto.mp3      # Tono corto para SoundPool
│   │   │   ├── otro_tono.mp3       # Segundo tono para SoundPool
│   │   │   └── cancion_larga.mp3   # Canción completa para MediaPlayer
│   │   ├── values/                  # Recursos de la aplicación
│   │   └── drawable/                # Iconos y gráficos
│   └── AndroidManifest.xml          # Configuración de la aplicación
```

## 🚀 Instalación y Configuración

### Prerrequisitos
- Android Studio Arctic Fox o superior
- Android SDK API 21+
- Dispositivo Android o emulador

### Pasos de Instalación
1. Clona el repositorio:
   ```bash
   git clone [URL_DEL_REPOSITORIO]
   cd appMultimedia
   ```

2. Abre el proyecto en Android Studio

3. Sincroniza las dependencias de Gradle

4. Conecta un dispositivo Android o inicia un emulador

5. Ejecuta la aplicación con `Shift + F10`

## 📋 Permisos Requeridos

La aplicación solicita los siguientes permisos:
- `RECORD_AUDIO`: Para grabar audio desde el micrófono
- `WRITE_EXTERNAL_STORAGE`: Para guardar grabaciones (Android < 10)

## 🎯 Uso de la Aplicación

### Pantalla de Bienvenida
- Muestra información de los desarrolladores
- Botón para acceder a la funcionalidad principal

### Pantalla Principal de Audio
1. **Reproducción de Tonos**: Botones para reproducir efectos de sonido
2. **Reproducción de Canción**: Control para reproducir/detener música
3. **Grabación**: Botón para iniciar/detener grabación de audio
4. **Reproducción de Grabación**: Escuchar la grabación realizada
5. **Log de Actividad**: Registro en tiempo real de todas las operaciones

## 🔍 Arquitectura del Código

### AudioViewModel
- **Estado**: Maneja el estado de reproducción, grabación y logs
- **Lógica de Negocio**: Centraliza toda la funcionalidad de audio
- **Ciclo de Vida**: Gestiona recursos multimedia correctamente

### MainActivity
- **Navegación**: Implementa navegación entre pantallas
- **Permisos**: Solicita y verifica permisos de audio
- **Composición**: Utiliza Jetpack Compose para la interfaz

## 🎨 Personalización

### Colores y Temas
Los colores se pueden personalizar en `app/src/main/res/values/colors.xml`:
```xml
<color name="primary">#FF6200EE</color>
<color name="secondary">#FF03DAC5</color>
```

### Archivos de Audio
Para cambiar los sonidos, reemplaza los archivos en `res/raw/`:
- `tono_corto.mp3`: Efecto de sonido 1
- `otro_tono.mp3`: Efecto de sonido 2
- `cancion_larga.mp3`: Música de fondo

## 🐛 Solución de Problemas

### Error de Permisos
- Asegúrate de que la aplicación tenga permisos de micrófono
- En Android 6+, los permisos se solicitan en tiempo de ejecución

### Problemas de Audio
- Verifica que los archivos de audio estén en formato compatible
- Asegúrate de que el dispositivo tenga altavoces o auriculares

### Problemas de Grabación
- Verifica que el micrófono esté funcionando
- Asegúrate de tener espacio de almacenamiento suficiente

## 📱 Compatibilidad

- **Android API**: 21+ (Android 5.0 Lollipop)
- **Arquitectura**: ARM, x86, x86_64
- **Orientación**: Portrait y Landscape

## 🤝 Contribución

1. Fork el proyecto
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

## 📄 Licencia

Este proyecto está bajo la Licencia MIT. Ver el archivo `LICENSE` para más detalles.

## 👥 Desarrolladores

- **Victor Tejeda** 

## 📞 Soporte

Si tienes alguna pregunta o problema:
- Abre un issue en el repositorio
- Contacta a los desarrolladores
- Revisa la documentación de Android

---

**Nota**: Esta aplicación es un proyecto educativo que demuestra el uso de APIs multimedia de Android. Para uso en producción, se recomienda implementar manejo de errores más robusto y validaciones adicionales.
