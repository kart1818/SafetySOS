package com.example.safetysos

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.telephony.SmsManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.net.Uri
import kotlin.math.sqrt
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MaterialTheme { SosScreen() } }
    }
}@Composable
fun SosScreen() {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("sos", Context.MODE_PRIVATE) }
    var number by remember { mutableStateOf(prefs.getString("number", "") ?: "") }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        if (result.values.all { it }) sendSos(context, number)
        else Toast.makeText(context, "Permission dena zaroori hai", Toast.LENGTH_LONG).show()
    }

    val triggerSos = {
        if (number.isBlank()) {
            Toast.makeText(context, "Pehle contact save karo", Toast.LENGTH_SHORT).show()
        } else {
            val sms = ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS)
            val loc = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
            if (sms == PackageManager.PERMISSION_GRANTED && loc == PackageManager.PERMISSION_GRANTED) {
                sendSos(context, number)
            } else {
                permissionLauncher.launch(
                    arrayOf(Manifest.permission.SEND_SMS, Manifest.permission.ACCESS_FINE_LOCATION)
                )
            }
        }
    }

    ShakeDetector(onShake = triggerSos)

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        OutlinedTextField(
            value = number,
            onValueChange = { number = it },
            label = { Text("Emergency contact number") },
            singleLine = true
        )
        Spacer(Modifier.height(12.dp))
        Button(onClick = {
            prefs.edit().putString("number", number).apply()
            Toast.makeText(context, "Contact save ho gaya", Toast.LENGTH_SHORT).show()
        }) { Text("Save contact") }

        Spacer(Modifier.height(48.dp))

        Button(
            onClick = { triggerSos() },
            modifier = Modifier.size(200.dp),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
        ) { Text("SOS", fontSize = 40.sp, color = Color.White) }

        Spacer(Modifier.height(24.dp))

        OutlinedButton(onClick = {
            context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$number")))
        }) { Text("Call contact") }
    }
}

@Composable
fun ShakeDetector(onShake: () -> Unit) {
    val context = LocalContext.current
    val currentOnShake by rememberUpdatedState(onShake)
    DisposableEffect(Unit) {
        val sm = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val accel = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        var lastShake = 0L
        val listener = object : SensorEventListener {
            override fun onSensorChanged(e: SensorEvent) {
                val g = sqrt(e.values[0] * e.values[0] + e.values[1] * e.values[1] + e.values[2] * e.values[2]) /
                        SensorManager.GRAVITY_EARTH
                val now = System.currentTimeMillis()
                if (g > 2.7f && now - lastShake > 3000) {
                    lastShake = now
                    currentOnShake()
                }
            }
            override fun onAccuracyChanged(s: Sensor?, a: Int) {}
        }
        sm.registerListener(listener, accel, SensorManager.SENSOR_DELAY_UI)
        onDispose { sm.unregisterListener(listener) }
    }
}

@SuppressLint("MissingPermission")
fun sendSos(context: Context, number: String) {
    val client = LocationServices.getFusedLocationProviderClient(context)
    client.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, CancellationTokenSource().token)
        .addOnSuccessListener { loc ->
            val msg = if (loc != null)
                "HELP! I am in danger. My location: https://maps.google.com/?q=${loc.latitude},${loc.longitude}"
            else
                "HELP! I am in danger. Meri location nahi mil paayi."
            try {
                val sms = context.getSystemService(SmsManager::class.java)
                sms.sendTextMessage(number, null, msg, null, null)
                Toast.makeText(context, "SMS bhejne ki koshish ki: $number", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Toast.makeText(context, "SMS fail: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
        .addOnFailureListener {
            Toast.makeText(context, "Location error: ${it.message}", Toast.LENGTH_LONG).show()
        }
}