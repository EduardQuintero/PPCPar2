package com.example.ppcpar2.Views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ppcpar2.Models.Lugar
import com.example.ppcpar2.ViewModels.ViajeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MiViajeScreen(
    viajeId: String,
    viewModel: ViajeViewModel,
    onBack: () -> Unit,
    onNavigateToAddLugar: (String) -> Unit
) {
    val selectedViaje by viewModel.selectedViaje.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(viajeId) {
        viewModel.cargarDetalleViaje(viajeId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(selectedViaje?.nombre ?: "Detalle del Viaje") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        floatingActionButton = {
            selectedViaje?.let { viaje ->
                FloatingActionButton(onClick = { onNavigateToAddLugar(viaje.id) }) {
                    Icon(Icons.Default.Add, contentDescription = "Agregar Lugar")
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (isLoading && selectedViaje == null) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (selectedViaje == null) {
                Text("No se pudo cargar el viaje", modifier = Modifier.align(Alignment.Center))
            } else {
                val viaje = selectedViaje!!
                Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    Text(text = "Destino: ${viaje.destino}", style = MaterialTheme.typography.titleMedium)
                    Text(text = "Fechas: ${viaje.fechaInicio} al ${viaje.fechaFin}", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "Lugares a visitar (${viaje.lugares.size}):", style = MaterialTheme.typography.titleSmall)
                    Spacer(modifier = Modifier.height(8.dp))

                    if (viaje.lugares.isEmpty()) {
                        Text("No hay lugares agregados todavía.", style = MaterialTheme.typography.bodyMedium)
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(viaje.lugares) { lugar ->
                                LugarItem(lugar = lugar)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LugarItem(lugar: Lugar) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = lugar.nombre, style = MaterialTheme.typography.titleMedium)
                Text(text = lugar.categoria.nombre, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = lugar.descripcion, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Ciudad: ${lugar.ciudad} | Fecha: ${lugar.fechaTentativa}", style = MaterialTheme.typography.bodySmall)
        }
    }
}
