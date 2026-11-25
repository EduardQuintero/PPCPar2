package com.example.ppcpar2.Models

import java.time.LocalDate

data class Viaje(
    val id: Int = 0,
    val nombre: String,
    val destino: String,
    val fechaInicio: LocalDate,
    val fechaFin: LocalDate,
    val lugares: List<Lugar> = emptyList()
)

data class Lugar(
    val id: Int = 0,
    val viajeId: Int,
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