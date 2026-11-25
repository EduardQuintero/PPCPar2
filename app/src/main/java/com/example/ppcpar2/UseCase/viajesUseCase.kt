package com.example.ppcpar2.UseCase

import com.example.ppcpar2.Models.Categoria
import com.example.ppcpar2.Models.Lugar
import com.example.ppcpar2.Models.Viaje
import com.example.ppcpar2.Repository.ViajeRepository

class ObtenerViajesUseCase(private val repository: ViajeRepository) {
    suspend operator fun invoke(): Result<List<Viaje>> = repository.obtenerViajes()
}

class ObtenerDetalleViajeUseCase(private val repository: ViajeRepository) {
    suspend operator fun invoke(viajeId: Int): Result<Viaje> = repository.obtenerDetalleViaje(viajeId)
}

class ObtenerCategoriasUseCase(private val repository: ViajeRepository) {
    suspend operator fun invoke(): Result<List<Categoria>> = repository.obtenerCategorias()
}

class CrearViajeUseCase(private val repository: ViajeRepository) {
    suspend operator fun invoke(viaje: Viaje): Result<Viaje> {
        if (viaje.nombre.isBlank()) {
            return Result.failure(IllegalArgumentException("El nombre del viaje es obligatorio."))
        }
        if (viaje.destino.isBlank()) {
            return Result.failure(IllegalArgumentException("El destino es obligatorio."))
        }
        if (viaje.fechaFin.isBefore(viaje.fechaInicio)) {
            return Result.failure(
                IllegalArgumentException("La fecha de finalización no puede ser anterior a la de inicio.")
            )
        }
        return repository.crearViaje(viaje)
    }
}

class AgregarLugarUseCase(private val repository: ViajeRepository) {
    suspend operator fun invoke(viaje: Viaje, lugar: Lugar): Result<Lugar> {
        if (lugar.nombre.isBlank()) {
            return Result.failure(IllegalArgumentException("El nombre del lugar es obligatorio."))
        }
        if (lugar.descripcion.isBlank()) {
            return Result.failure(IllegalArgumentException("La descripción del lugar es obligatoria."))
        }
        if (lugar.ciudad.isBlank()) {
            return Result.failure(IllegalArgumentException("La ciudad del lugar es obligatoria."))
        }
        if (lugar.categoria.nombre.isBlank()) {
            return Result.failure(IllegalArgumentException("Debe seleccionar una categoría."))
        }
        val fueraDeRango = lugar.fechaTentativa.isBefore(viaje.fechaInicio) ||
                lugar.fechaTentativa.isAfter(viaje.fechaFin)
        if (fueraDeRango) {
            return Result.failure(
                IllegalArgumentException(
                    "La fecha tentativa (${lugar.fechaTentativa}) debe estar entre ${viaje.fechaInicio} y ${viaje.fechaFin}."
                )
            )
        }
        return repository.agregarLugar(viaje.id, lugar)
    }
}