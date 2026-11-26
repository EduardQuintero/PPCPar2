package com.example.ppcpar2.Models

import java.time.LocalDate

data class Viaje(
    val id: String = "",
    val nombre: String,
    val destino: String,
    val fechaInicio: LocalDate,
    val fechaFin: LocalDate,
    val lugares: List<Lugar> = emptyList()
)

data class Lugar(
    val id: String = "",
    val viajeId: String,
    val nombre: String,
    val descripcion: String,
    val ciudad: String,
    val categoria: Categoria,
    val fechaTentativa: LocalDate
)

data class Categoria(
    val id: Int,
    val nombre: String
)
