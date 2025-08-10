package com.example.myapplication09

// ============================================================================
// IMPORTS NECESARIOS PARA LA APLICACIÓN
// ============================================================================
import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.media.SoundPool
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication09.ui.theme.MyApplication09Theme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream
import java.io.IOException

// ============================================================================
// VIEWMODEL PRINCIPAL - MANEJA TODA LA LÓGICA DE AUDIO
// ============================================================================
class AudioViewModel : ViewModel() {
    
    // ===== ESTADOS OBSERVABLES =====
    // Estos son los estados que la UI puede observar y reaccionar a cambios
    
    // Lista de mensajes de log para mostrar en pantalla
    private val _logMessages = MutableStateFlow<List<String>>(listOf("🎵 Iniciando aplicación multimedia..."))
    val logMessages: StateFlow<List<String>> = _logMessages.asStateFlow()

    // Estado del reproductor de música principal
    private val _isMediaPlayerPlaying = MutableStateFlow(false)
    val isMediaPlayerPlaying: StateFlow<Boolean> = _isMediaPlayerPlaying.asStateFlow()

    // Estado de grabación (si está grabando o no)
    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording.asStateFlow()

    // Si hay una grabación disponible para reproducir
    private val _isRecordingAvailable = MutableStateFlow(false)
    val isRecordingAvailable: StateFlow<Boolean> = _isRecordingAvailable.asStateFlow()

    // Estado de reproducción de la grabación
    private val _isPlaybackPlaying = MutableStateFlow(false)
    val isPlaybackPlaying: StateFlow<Boolean> = _isPlaybackPlaying.asStateFlow()

    // ===== VARIABLES PRIVADAS =====
    // Estas variables manejan los objetos multimedia internamente
    
    private var soundPool: SoundPool? = null           // Para efectos de sonido cortos
    private var songMediaPlayer: MediaPlayer? = null   // Para música larga
    private var recordingMediaPlayer: MediaPlayer? = null // Para reproducir grabaciones
    private var mediaRecorder: MediaRecorder? = null   // Para grabar audio
    
    private var soundId1: Int = 0  // ID del primer tono
    private var soundId2: Int = 0  // ID del segundo tono
    private var audioFile: File? = null // Archivo de grabación temporal

    // ===== INICIALIZACIÓN =====
    // Se ejecuta cuando se crea el ViewModel
    init {
        // Configuramos SoundPool para efectos de sonido
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)           // Uso para juegos (mejor para efectos)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION) // Tipo de contenido
            .build()
        
        // Creamos SoundPool con máximo 2 streams simultáneos
        soundPool = SoundPool.Builder()
            .setMaxStreams(2)
            .setAudioAttributes(audioAttributes)
            .build()
        
        addLog("🔧 SoundPool inicializado correctamente")
    }

    // ===== FUNCIONES PÚBLICAS PARA LA UI =====
    
    /**
     * Carga los archivos de sonido en SoundPool
     * @param context Contexto de la aplicación
     */
    fun loadSounds(context: Context) {
        try {
            // Cargamos los dos tonos desde la carpeta raw
            soundId1 = soundPool?.load(context, R.raw.tono_corto, 1) ?: 0
            soundId2 = soundPool?.load(context, R.raw.otro_tono, 1) ?: 0
            
            addLog("✅ Tonos de SoundPool cargados exitosamente")
        } catch (e: Exception) {
            addLog("❌ Error: Asegúrate de tener 'tono_corto.mp3' y 'otro_tono.mp3' en res/raw")
        }
    }

    /**
     * Reproduce un tono específico usando SoundPool
     * @param soundId ID del tono (1 o 2)
     */
    fun playSoundPool(soundId: Int) {
        val idToPlay = if (soundId == 1) soundId1 else soundId2
        
        if (idToPlay > 0) {
            // Reproducimos el sonido con volumen máximo y sin loop
            soundPool?.play(idToPlay, 1f, 1f, 1, 0, 1f)
            addLog("🔊 Reproduciendo tono de SoundPool #$soundId")
        } else {
            addLog("❌ Error: Tono #$soundId no está cargado")
        }
    }

    /**
     * Maneja la reproducción/detención de la canción principal
     * @param context Contexto de la aplicación
     */
    fun handleMediaPlayer(context: Context) {
        if (songMediaPlayer?.isPlaying == true) {
            // Si está reproduciendo, lo detenemos
            songMediaPlayer?.stop()
            songMediaPlayer?.release()
            songMediaPlayer = null
            _isMediaPlayerPlaying.value = false
            addLog("⏹️ Reproducción de canción detenida")
        } else {
            // Si no está reproduciendo, iniciamos la música
            try {
                songMediaPlayer = MediaPlayer.create(context, R.raw.cancion_larga).apply {
                    // Configuramos el listener para cuando termine la canción
                    setOnCompletionListener {
                        _isMediaPlayerPlaying.value = false
                        addLog("🏁 Fin de la reproducción de la canción")
                    }
                    start() // Iniciamos la reproducción
                }
                _isMediaPlayerPlaying.value = true
                addLog("▶️ Reproduciendo canción con MediaPlayer")
            } catch (e: Exception) {
                addLog("❌ Error: Asegúrate de tener 'cancion_larga.mp3' en res/raw")
            }
        }
    }

    /**
     * Maneja el inicio/detención de la grabación
     * @param context Contexto de la aplicación
     */
    fun handleRecording(context: Context) {
        if (_isRecording.value) {
            stopRecording(context)
        } else {
            startRecording(context)
        }
    }

    // ===== FUNCIONES PRIVADAS =====
    
    /**
     * Inicia la grabación de audio
     * @param context Contexto de la aplicación
     */
    private fun startRecording(context: Context) {
        _isRecordingAvailable.value = false // No hay grabación disponible mientras grabamos
        
        val outputDir = context.cacheDir // Usamos el directorio de cache
        
        try {
            // Creamos un archivo temporal para la grabación
            audioFile = File.createTempFile("grabacion", ".3gp", outputDir)
        } catch (e: IOException) {
            addLog("❌ Error al crear archivo de grabación")
            return
        }
        
        // Creamos el MediaRecorder (con compatibilidad para diferentes versiones de Android)
        mediaRecorder = (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else {
            @Suppress("DEPRECATION") MediaRecorder()
        }).apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)        // Fuente: micrófono
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP) // Formato: 3GP
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)   // Codificador: AMR
            setOutputFile(audioFile?.absolutePath)               // Archivo de salida
            
            try {
                prepare() // Preparamos el recorder
                start()   // Iniciamos la grabación
                _isRecording.value = true
                addLog("🎤 Grabando conversación...")
            } catch (e: IOException) {
                addLog("❌ Error al iniciar grabación: ${e.message}")
            }
        }
    }

    /**
     * Detiene la grabación de audio
     * @param context Contexto de la aplicación
     */
    private fun stopRecording(context: Context) {
        try {
            mediaRecorder?.stop()    // Detenemos la grabación
            mediaRecorder?.release() // Liberamos recursos
        } catch (e: Exception) {
            addLog("⚠️ Detención de grabación forzada")
        }
        
        mediaRecorder = null
        _isRecording.value = false
        _isRecordingAvailable.value = true // Ahora hay una grabación disponible
        
        addLog("⏹️ Grabación detenida")
        addRecordingToMediaLibrary(context) // Guardamos en la librería multimedia
    }

    /**
     * Reproduce la grabación realizada
     * @param context Contexto de la aplicación
     */
    fun playRecording(context: Context) {
        if (recordingMediaPlayer?.isPlaying == true) {
            // Si está reproduciendo, lo detenemos
            recordingMediaPlayer?.stop()
            recordingMediaPlayer?.release()
            recordingMediaPlayer = null
            _isPlaybackPlaying.value = false
            addLog("⏹️ Reproducción de grabación detenida")
            return
        }

        // Verificamos que exista el archivo de grabación
        if (audioFile?.exists() == true) {
            recordingMediaPlayer = MediaPlayer()
            var fis: FileInputStream? = null
            
            try {
                fis = FileInputStream(audioFile)
                recordingMediaPlayer?.setDataSource(fis.fd)
                recordingMediaPlayer?.prepare()
                recordingMediaPlayer?.start()
                _isPlaybackPlaying.value = true
                addLog("▶️ Reproduciendo grabación")
                
                // Configuramos el listener para cuando termine
                recordingMediaPlayer?.setOnCompletionListener {
                    _isPlaybackPlaying.value = false
                    addLog("🏁 Fin de la reproducción de la grabación")
                }
            } catch (e: IOException) {
                addLog("❌ Error al reproducir grabación: ${e.message}")
            } finally {
                fis?.close() // Siempre cerramos el stream
            }
        } else {
            addLog("❌ No se encontró archivo de grabación")
        }
    }

    /**
     * Guarda la grabación en la librería multimedia del dispositivo
     * @param context Contexto de la aplicación
     */
    private fun addRecordingToMediaLibrary(context: Context) {
        if (audioFile == null) return
        
        // Creamos los valores para insertar en MediaStore
        val values = ContentValues().apply {
            put(MediaStore.Audio.Media.TITLE, audioFile!!.name)
            put(MediaStore.Audio.Media.DISPLAY_NAME, audioFile!!.name)
            put(MediaStore.Audio.Media.MIME_TYPE, "audio/3gpp")
            put(MediaStore.Audio.Media.DATE_ADDED, System.currentTimeMillis() / 1000)
            
            // Para Android 10+ usamos RELATIVE_PATH, para versiones anteriores DATA
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Audio.Media.RELATIVE_PATH, "Music/Recordings/")
                put(MediaStore.Audio.Media.IS_PENDING, 1)
            } else {
                put(MediaStore.Audio.Media.DATA, audioFile!!.absolutePath)
            }
        }
        
        // Insertamos en MediaStore
        val uri = context.contentResolver.insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values)
        
        try {
            uri?.let {
                // Copiamos el archivo de audio
                context.contentResolver.openOutputStream(it).use { outputStream ->
                    audioFile?.inputStream()?.copyTo(outputStream!!)
                }
                
                // Para Android 10+, marcamos como no pendiente
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    values.clear()
                    values.put(MediaStore.Audio.Media.IS_PENDING, 0)
                    context.contentResolver.update(it, values, null, null)
                }
                
                addLog("💾 Grabación guardada en la librería multimedia")
                Toast.makeText(context, "Grabación guardada", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            addLog("❌ Error al guardar grabación: ${e.message}")
        }
    }

    /**
     * Agrega un mensaje al log
     * @param message Mensaje a agregar
     */
    private fun addLog(message: String) {
        viewModelScope.launch {
            _logMessages.value = _logMessages.value + message
        }
    }

    /**
     * Limpia recursos cuando se destruye el ViewModel
     */
    override fun onCleared() {
        super.onCleared()
        // Liberamos todos los recursos multimedia
        soundPool?.release()
        songMediaPlayer?.release()
        recordingMediaPlayer?.release()
        mediaRecorder?.release()
        addLog("🧹 Recursos liberados")
    }
}

// ============================================================================
// ACTIVIDAD PRINCIPAL - PUNTO DE ENTRADA DE LA APLICACIÓN
// ============================================================================
class MainActivity : ComponentActivity() {
    
    // Instancia del ViewModel que maneja toda la lógica
    private val viewModel = AudioViewModel()
    
    // Launcher para solicitar permisos de audio
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                // Si se otorga el permiso, iniciamos la grabación
                viewModel.handleRecording(this)
            } else {
                // Si se deniega, mostramos un mensaje
                Toast.makeText(this, "Permiso para grabar denegado", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Cargamos los sonidos al iniciar la aplicación
        viewModel.loadSounds(this)
        
        // Configuramos la UI con Jetpack Compose
        setContent {
            MyApplication09Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(
                        viewModel = viewModel,
                        checkAndRequestPermission = { checkAndRequestAudioPermission() }
                    )
                }
            }
        }
    }

    /**
     * Verifica y solicita permisos de audio si es necesario
     */
    private fun checkAndRequestAudioPermission() {
        when {
            // Si ya tenemos el permiso, iniciamos la grabación
            ContextCompat.checkSelfPermission(
                this, 
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED -> {
                viewModel.handleRecording(this)
            }
            // Si no tenemos el permiso, lo solicitamos
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        }
    }
}

// ============================================================================
// NAVEGACIÓN PRINCIPAL - MANEJA LAS DIFERENTES PANTALLAS
// ============================================================================
@Composable
fun AppNavigation(
    viewModel: AudioViewModel, 
    checkAndRequestPermission: () -> Unit
) {
    val navController = rememberNavController()
    
    // Configuramos las rutas de navegación
    NavHost(navController = navController, startDestination = "welcome_screen") {
        // Pantalla de bienvenida
        composable("welcome_screen") { 
            WelcomeScreen(navController = navController) 
        }
        // Pantalla principal de audio
        composable("audio_screen") {
            AudioProjectScreen(
                navController = navController,
                viewModel = viewModel,
                onRequestPermission = checkAndRequestPermission
            )
        }
    }
}

// ============================================================================
// PANTALLA DE BIENVENIDA - MUESTRA INFORMACIÓN DE LOS DESARROLLADORES
// ============================================================================
@Composable
fun WelcomeScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Título principal
        Text(
            text = "🎵 Proyecto de Multimedia",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Subtítulo
        Text(
            text = "Aplicación de Audio y Grabación",
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Tarjetas de desarrolladores
        AuthorCard(
            name = "Henry Castro",
            role = "1-21-4112",
            icon = Icons.Default.Person
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        AuthorCard(
            name = "Lissette Rodríguez",
            role = "1-19-3824",
            icon = Icons.Default.Person
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        AuthorCard(
            name = "Miguel Berroa",
            role = "2-16-3694",
            icon = Icons.Default.Person
        )
        
        Spacer(modifier = Modifier.height(48.dp))
        
        // Botón para ir a la funcionalidad principal
        Button(
            onClick = { navController.navigate("audio_screen") },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Ir al Proyecto de Audio",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

// ============================================================================
// PANTALLA PRINCIPAL DE AUDIO - TODA LA FUNCIONALIDAD MULTIMEDIA
// ============================================================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioProjectScreen(
    navController: NavController,
    viewModel: AudioViewModel,
    onRequestPermission: () -> Unit
) {
    val context = LocalContext.current
    
    // ===== ESTADOS OBSERVABLES =====
    val logMessages by viewModel.logMessages.collectAsState()
    val isMediaPlayerPlaying by viewModel.isMediaPlayerPlaying.collectAsState()
    val isRecording by viewModel.isRecording.collectAsState()
    val isRecordingAvailable by viewModel.isRecordingAvailable.collectAsState()
    val isPlaybackPlaying by viewModel.isPlaybackPlaying.collectAsState()
    
    // Estado para el scroll automático del log
    val lazyListState = rememberLazyListState()

    // Scroll automático cuando hay nuevos mensajes
    LaunchedEffect(logMessages.size) {
        if (logMessages.isNotEmpty()) {
            lazyListState.animateScrollToItem(logMessages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "🎵 Proyecto de Audio",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ===== SECCIÓN DE TONOS =====
            AudioSection(
                title = "🔊 Efectos de Sonido",
                description = "Reproduce tonos cortos con SoundPool"
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    AudioButton(
                        text = "Tono 1",
                        icon = Icons.Default.MusicNote,
                        onClick = { viewModel.playSoundPool(1) },
                        modifier = Modifier.weight(1f)
                    )
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    AudioButton(
                        text = "Tono 2",
                        icon = Icons.Default.MusicNote,
                        onClick = { viewModel.playSoundPool(2) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // ===== SECCIÓN DE MÚSICA =====
            AudioSection(
                title = "🎵 Reproducción de Música",
                description = "Controla la reproducción de canciones largas"
            ) {
                AudioButton(
                    text = if (isMediaPlayerPlaying) "⏹️ Detener Canción" else "▶️ Reproducir Canción",
                    icon = if (isMediaPlayerPlaying) Icons.Default.Stop else Icons.Default.PlayArrow,
                    onClick = { viewModel.handleMediaPlayer(context) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isRecording && !isPlaybackPlaying,
                    colors = if (isMediaPlayerPlaying) {
                        ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    } else {
                        ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    }
                )
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // ===== SECCIÓN DE GRABACIÓN =====
            AudioSection(
                title = "🎤 Grabación de Audio",
                description = "Graba conversaciones desde el micrófono"
            ) {
                AudioButton(
                    text = if (isRecording) "⏹️ Detener Grabación" else "🎤 Grabar Conversación",
                    icon = if (isRecording) Icons.Default.Stop else Icons.Default.Mic,
                    onClick = { onRequestPermission() },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isMediaPlayerPlaying && !isPlaybackPlaying,
                    colors = if (isRecording) {
                        ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    } else {
                        ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    }
                )
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // ===== SECCIÓN DE REPRODUCCIÓN DE GRABACIÓN =====
            AudioSection(
                title = "📻 Reproducir Grabación",
                description = "Escucha la grabación realizada"
            ) {
                AudioButton(
                    text = if (isPlaybackPlaying) "⏹️ Detener Grabación" else "▶️ Reproducir Grabación",
                    icon = if (isPlaybackPlaying) Icons.Default.Stop else Icons.Default.PlayArrow,
                    onClick = { viewModel.playRecording(context) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = isRecordingAvailable && !isRecording && !isMediaPlayerPlaying,
                    colors = if (isPlaybackPlaying) {
                        ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    } else {
                        ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            
            // ===== SECCIÓN DE LOG =====
            AudioSection(
                title = "📋 Registro de Actividad",
                description = "Historial de todas las operaciones realizadas"
            ) {
                LazyColumn(
                    state = lazyListState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(vertical = 8.dp)
                ) {
                    items(logMessages) { message ->
                        Text(
                            text = message,
                            modifier = Modifier.padding(vertical = 2.dp, horizontal = 8.dp),
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

// ============================================================================
// COMPONENTES REUTILIZABLES - MEJORAN LA ORGANIZACIÓN DEL CÓDIGO
// ============================================================================

/**
 * Tarjeta que muestra información de un desarrollador
 */
@Composable
fun AuthorCard(
    name: String,
    role: String,
    icon: ImageVector
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = role,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
        }
    }
}

/**
 * Sección de audio con título y descripción
 */
@Composable
fun AudioSection(
    title: String,
    description: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = description,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            content()
        }
    }
}

/**
 * Botón personalizado para controles de audio
 */
@Composable
fun AudioButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: ButtonColors = ButtonDefaults.buttonColors()
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        enabled = enabled,
        colors = colors
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp)
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}