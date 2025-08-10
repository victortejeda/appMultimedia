package com.example.myapplication09 // O el nombre de tu paquete

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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

// --- VIEWMODEL PARA MANEJAR TODA LA LÓGICA DE AUDIO ---
class AudioViewModel : ViewModel() {
    private val _logMessages = MutableStateFlow<List<String>>(listOf("Log:"))
    val logMessages: StateFlow<List<String>> = _logMessages.asStateFlow()

    private val _isMediaPlayerPlaying = MutableStateFlow(false)
    val isMediaPlayerPlaying: StateFlow<Boolean> = _isMediaPlayerPlaying.asStateFlow()

    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording.asStateFlow()

    private val _isRecordingAvailable = MutableStateFlow(false)
    val isRecordingAvailable: StateFlow<Boolean> = _isRecordingAvailable.asStateFlow()

    private val _isPlaybackPlaying = MutableStateFlow(false)
    val isPlaybackPlaying: StateFlow<Boolean> = _isPlaybackPlaying.asStateFlow()

    private var soundPool: SoundPool? = null
    private var songMediaPlayer: MediaPlayer? = null
    private var recordingMediaPlayer: MediaPlayer? = null
    private var mediaRecorder: MediaRecorder? = null

    private var soundId1: Int = 0
    private var soundId2: Int = 0
    private var audioFile: File? = null

    init {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        soundPool = SoundPool.Builder().setMaxStreams(2).setAudioAttributes(audioAttributes).build()
    }

    fun loadSounds(context: Context) {
        try {
            soundId1 = soundPool?.load(context, R.raw.tono_corto, 1) ?: 0
            soundId2 = soundPool?.load(context, R.raw.otro_tono, 1) ?: 0
            addLog("Tonos de SoundPool cargados.")
        } catch (e: Exception) {
            addLog("Error: Asegúrate de tener 'tono_corto.mp3' y 'otro_tono.mp3' en res/raw.")
        }
    }

    fun playSoundPool(soundId: Int) {
        val idToPlay = if (soundId == 1) soundId1 else soundId2
        if (idToPlay > 0) {
            soundPool?.play(idToPlay, 1f, 1f, 1, 0, 1f)
            addLog("Reproduciendo tono de SoundPool #$soundId")
        } else {
            addLog("Error: Tono #$soundId no cargado.")
        }
    }

    fun handleMediaPlayer(context: Context) {
        if (songMediaPlayer?.isPlaying == true) {
            songMediaPlayer?.stop()
            songMediaPlayer?.release()
            songMediaPlayer = null
            _isMediaPlayerPlaying.value = false
            addLog("Reproducción de canción detenida.")
        } else {
            try {
                songMediaPlayer = MediaPlayer.create(context, R.raw.cancion_larga).apply {
                    setOnCompletionListener {
                        _isMediaPlayerPlaying.value = false
                        addLog("Fin de la reproducción de la canción.")
                    }
                    start()
                }
                _isMediaPlayerPlaying.value = true
                addLog("Reproduciendo canción con MediaPlayer.")
            } catch (e: Exception) {
                addLog("Error: Asegúrate de tener 'cancion_larga.mp3' en res/raw.")
            }
        }
    }

    fun handleRecording(context: Context) {
        if (_isRecording.value) {
            stopRecording(context)
        } else {
            startRecording(context)
        }
    }

    private fun startRecording(context: Context) {
        _isRecordingAvailable.value = false
        val outputDir = context.cacheDir
        try {
            audioFile = File.createTempFile("grabacion", ".3gp", outputDir)
        } catch (e: IOException) {
            addLog("Error al crear archivo de grabación.")
            return
        }
        mediaRecorder = (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) MediaRecorder(context)
        else @Suppress("DEPRECATION") MediaRecorder()).apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setOutputFile(audioFile?.absolutePath)
            try {
                prepare()
                start()
                _isRecording.value = true
                addLog("Grabando conversación...")
            } catch (e: IOException) {
                addLog("Error al iniciar grabación: ${e.message}")
            }
        }
    }

    private fun stopRecording(context: Context) {
        try {
            mediaRecorder?.stop()
            mediaRecorder?.release()
        } catch (e: Exception) {
            addLog("Detención de grabación forzada.")
        }
        mediaRecorder = null
        _isRecording.value = false
        _isRecordingAvailable.value = true
        addLog("Grabación detenida.")
        addRecordingToMediaLibrary(context)
    }

    // --- FUNCIÓN CORREGIDA PARA REPRODUCIR LA GRABACIÓN ---
    fun playRecording(context: Context) {
        if (recordingMediaPlayer?.isPlaying == true) {
            recordingMediaPlayer?.stop()
            recordingMediaPlayer?.release()
            recordingMediaPlayer = null
            _isPlaybackPlaying.value = false
            addLog("Reproducción de grabación detenida.")
            return
        }

        if (audioFile?.exists() == true) {
            recordingMediaPlayer = MediaPlayer()
            var fis: FileInputStream? = null
            try {
                fis = FileInputStream(audioFile)
                recordingMediaPlayer?.setDataSource(fis.fd)
                recordingMediaPlayer?.prepare()
                recordingMediaPlayer?.start()
                _isPlaybackPlaying.value = true
                addLog("Reproduciendo grabación.")
                recordingMediaPlayer?.setOnCompletionListener {
                    _isPlaybackPlaying.value = false
                    addLog("Fin de la reproducción de la grabación.")
                }
            } catch (e: IOException) {
                addLog("Error al reproducir grabación: ${e.message}")
            } finally {
                fis?.close()
            }
        } else {
            addLog("No se encontró archivo de grabación.")
        }
    }

    private fun addRecordingToMediaLibrary(context: Context) {
        if (audioFile == null) return
        val values = ContentValues().apply {
            put(MediaStore.Audio.Media.TITLE, audioFile!!.name)
            put(MediaStore.Audio.Media.DISPLAY_NAME, audioFile!!.name)
            put(MediaStore.Audio.Media.MIME_TYPE, "audio/3gpp")
            put(MediaStore.Audio.Media.DATE_ADDED, System.currentTimeMillis() / 1000)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Audio.Media.RELATIVE_PATH, "Music/Recordings/")
                put(MediaStore.Audio.Media.IS_PENDING, 1)
            } else {
                put(MediaStore.Audio.Media.DATA, audioFile!!.absolutePath)
            }
        }
        val uri = context.contentResolver.insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values)
        try {
            uri?.let {
                context.contentResolver.openOutputStream(it).use { outputStream ->
                    audioFile?.inputStream()?.copyTo(outputStream!!)
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    values.clear()
                    values.put(MediaStore.Audio.Media.IS_PENDING, 0)
                    context.contentResolver.update(it, values, null, null)
                }
                addLog("Grabación guardada en la librería multimedia.")
                Toast.makeText(context, "Grabación guardada", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            addLog("Error al guardar grabación: ${e.message}")
        }
    }

    private fun addLog(message: String) {
        viewModelScope.launch { _logMessages.value = _logMessages.value + message }
    }

    override fun onCleared() {
        super.onCleared()
        soundPool?.release()
        songMediaPlayer?.release()
        recordingMediaPlayer?.release()
        mediaRecorder?.release()
    }
}

// --- ACTIVITY PRINCIPAL, NAVEGACIÓN Y PANTALLAS ---
// (El resto del código no tiene cambios, pero se incluye para que sea completo)

class MainActivity : ComponentActivity() {
    private val viewModel = AudioViewModel()
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                viewModel.handleRecording(this)
            } else {
                Toast.makeText(this, "Permiso para grabar denegado", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.loadSounds(this)
        setContent {
            MyApplication09Theme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    AppNavigation(
                        viewModel = viewModel,
                        checkAndRequestPermission = { checkAndRequestAudioPermission() }
                    )
                }
            }
        }
    }

    private fun checkAndRequestAudioPermission() {
        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED -> {
                viewModel.handleRecording(this)
            }
            else -> { requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO) }
        }
    }
}

@Composable
fun AppNavigation(viewModel: AudioViewModel, checkAndRequestPermission: () -> Unit) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "welcome_screen") {
        composable("welcome_screen") { WelcomeScreen(navController = navController) }
        composable("audio_screen") {
            AudioProjectScreen(
                navController = navController,
                viewModel = viewModel,
                onRequestPermission = checkAndRequestPermission
            )
        }
    }
}

@Composable
fun WelcomeScreen(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Proyecto de Multimedia", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(24.dp))
        AuthorCard(name = "Henry Castro", role = "1-21-4112")
        Spacer(modifier = Modifier.height(8.dp))
        AuthorCard(name = "Lissette Rodríguez", role = "1-19-3824")
        Spacer(modifier = Modifier.height(8.dp))
        AuthorCard(name = "Miguel Berroa", role = "2-16-3694")
        Spacer(modifier = Modifier.height(48.dp))
        Button(onClick = { navController.navigate("audio_screen") }) {
            Text("Ir al Proyecto de Audio")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioProjectScreen(
    navController: NavController,
    viewModel: AudioViewModel,
    onRequestPermission: () -> Unit
) {
    val context = LocalContext.current
    val logMessages by viewModel.logMessages.collectAsState()
    val isMediaPlayerPlaying by viewModel.isMediaPlayerPlaying.collectAsState()
    val isRecording by viewModel.isRecording.collectAsState()
    val isRecordingAvailable by viewModel.isRecordingAvailable.collectAsState()
    val isPlaybackPlaying by viewModel.isPlaybackPlaying.collectAsState()
    val lazyListState = rememberLazyListState()

    LaunchedEffect(logMessages.size) {
        if (logMessages.isNotEmpty()) {
            lazyListState.animateScrollToItem(logMessages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Proyecto de Audio") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding).padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                Button(onClick = { viewModel.playSoundPool(1) }) { Text("Tono 1") }
                Button(onClick = { viewModel.playSoundPool(2) }) { Text("Tono 2") }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { viewModel.handleMediaPlayer(context) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isRecording && !isPlaybackPlaying
            ) {
                Text(if (isMediaPlayerPlaying) "Detener Canción" else "Reproducir Canción")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { onRequestPermission() },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isMediaPlayerPlaying && !isPlaybackPlaying,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isRecording) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                )
            ) {
                Text(if (isRecording) "Detener Grabación" else "Grabar Conversación")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { viewModel.playRecording(context) },
                modifier = Modifier.fillMaxWidth(),
                enabled = isRecordingAvailable && !isRecording && !isMediaPlayerPlaying
            ) {
                Text(if (isPlaybackPlaying) "Detener Grabación" else "Reproducir Grabación")
            }

            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn(
                state = lazyListState,
                modifier = Modifier.fillMaxWidth().weight(1f).padding(vertical = 8.dp)
            ) {
                items(logMessages) { message ->
                    Text(message, modifier = Modifier.padding(2.dp))
                }
            }
        }
    }
}

@Composable
fun AuthorCard(name: String, role: String) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(text = role, style = MaterialTheme.typography.bodyMedium)
        }
    }
}