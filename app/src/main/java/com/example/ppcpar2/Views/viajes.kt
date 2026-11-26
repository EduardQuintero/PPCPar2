package com.example.ppcpar2.Views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ppcpar2.Models.Viaje
import com.example.ppcpar2.ViewModels.ViajeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViajesScreen(
    viewModel: ViajeViewModel,
    onNavigateToCrear: () -> Unit,
    onNavigateToDetalle: (String) -> Unit
) {
    val viajes by viewModel.viajes.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.cargarViajes()
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Mis Viajes") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToCrear) {
                Icon(Icons.Default.Add, contentDescription = "Crear Viaje")
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (isLoading && viajes.isEmpty()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (viajes.isEmpty()) {
                Text("No hay viajes registrados", modifier = Modifier.align(Alignment.Center))
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(viajes) { viaje ->
                        ViajeItem(viaje = viaje, onClick = { onNavigateToDetalle(viaje.id) })
                    }
                }
            }

            errorMessage?.let { error ->
                Snackbar(
                    modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp),
                    action = {
                        TextButton(onClick = { viewModel.cargarViajes() }) {
                            Text("Reintentar")
                        }
                    }
                ) {
                    Text(error)
                }
            }
        }
    }
}

@Composable
fun ViajeItem(viaje: Viaje, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = viaje.nombre, style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Destino: ${viaje.destino}", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Del ${viaje.fechaInicio} al ${viaje.fechaFin}", style = MaterialTheme.typography.bodySmall)
        }
    }
}
