package com.example.ppcpar2.Views

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ppcpar2.ViewModels.ViajeViewModel
import java.time.LocalDate
import java.time.format.DateTimeParseException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearViajeScreen(
    viewModel: ViajeViewModel,
    onBack: () -> Unit,
    onSuccess: () -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var destino by remember { mutableStateOf("") }
    var fechaInicioStr by remember { mutableStateOf("") }
    var fechaFinStr by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crear Nuevo Viaje") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre del viaje") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = destino,
                onValueChange = { destino = it },
                label = { Text("Destino") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = fechaInicioStr,
                onValueChange = { fechaInicioStr = it },
                label = { Text("Fecha Inicio (AAAA-MM-DD)") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = fechaFinStr,
                onValueChange = { fechaFinStr = it },
                label = { Text("Fecha Fin (AAAA-MM-DD)") },
                modifier = Modifier.fillMaxWidth()
            )

            errorMsg?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    try {
                        val inicio = LocalDate.parse(fechaInicioStr)
                        val fin = LocalDate.parse(fechaFinStr)
                        viewModel.crearViaje(nombre, destino, inicio, fin, onSuccess = onSuccess)
                    } catch (e: DateTimeParseException) {
                        errorMsg = "Formato de fecha inválido. Use AAAA-MM-DD"
                    } catch (e: Exception) {
                        errorMsg = e.message
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Guardar Viaje")
                }
            }
        }
    }
}
