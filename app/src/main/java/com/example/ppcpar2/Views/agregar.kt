package com.example.ppcpar2.Views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ppcpar2.Models.Categoria
import com.example.ppcpar2.ViewModels.ViajeViewModel
import java.time.LocalDate
import java.time.format.DateTimeParseException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgregarLugarScreen(
    viajeId: String,
    viewModel: ViajeViewModel,
    onBack: () -> Unit,
    onSuccess: () -> Unit
) {
    val selectedViaje by viewModel.selectedViaje.collectAsState()
    val categorias by viewModel.categorias.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var ciudad by remember { mutableStateOf("") }
    var fechaTentativaStr by remember { mutableStateOf("") }
    var selectedCategoria by remember { mutableStateOf<Categoria?>(null) }
    var expanded by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(viajeId) {
        if (selectedViaje == null || selectedViaje?.id != viajeId) {
            viewModel.cargarDetalleViaje(viajeId)
        }
        viewModel.cargarCategorias()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Agregar Lugar") },
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre del lugar") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = ciudad,
                onValueChange = { ciudad = it },
                label = { Text("Ciudad") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = fechaTentativaStr,
                onValueChange = { fechaTentativaStr = it },
                label = { Text("Fecha tentativa (AAAA-MM-DD)") },
                modifier = Modifier.fillMaxWidth()
            )

            // Category Dropdown
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = selectedCategoria?.nombre ?: "Seleccionar Categoría",
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    categorias.forEach { categoria ->
                        DropdownMenuItem(
                            text = { Text(categoria.nombre) },
                            onClick = {
                                selectedCategoria = categoria
                                expanded = false
                            }
                        )
                    }
                }
            }

            errorMsg?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val viaje = selectedViaje
                    val cat = selectedCategoria
                    if (viaje == null) {
                        errorMsg = "Viaje no cargado"
                        return@Button
                    }
                    if (cat == null) {
                        errorMsg = "Debe seleccionar una categoría"
                        return@Button
                    }
                    try {
                        val fechaTentativa = LocalDate.parse(fechaTentativaStr)
                        viewModel.agregarLugar(
                            viaje = viaje,
                            nombre = nombre,
                            descripcion = descripcion,
                            ciudad = ciudad,
                            categoria = cat,
                            fechaTentativa = fechaTentativa,
                            onSuccess = onSuccess
                        )
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
                    Text("Guardar Lugar")
                }
            }
        }
    }
}
