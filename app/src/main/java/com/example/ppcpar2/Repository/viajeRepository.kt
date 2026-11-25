package com.example.ppcpar2.Repository

import com.example.ppcpar2.Models.Categoria
import com.example.ppcpar2.Models.Lugar
import com.example.ppcpar2.Models.Viaje
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import java.time.LocalDate

interface ViajeRepository {
    suspend fun obtenerViajes(): Result<List<Viaje>>
    suspend fun obtenerDetalleViaje(viajeId: Int): Result<Viaje>
    suspend fun crearViaje(viaje: Viaje): Result<Viaje>
    suspend fun agregarLugar(viajeId: Int, lugar: Lugar): Result<Lugar>
    suspend fun obtenerCategorias(): Result<List<Categoria>>
}

data class CategoriaDto(
    val id: Int,
    val nombre: String
) {
    fun toDomain() = Categoria(id = id, nombre = nombre)
}

data class LugarDto(
    val id: Int = 0,
    val viajeId: Int = 0,
    val nombre: String,
    val descripcion: String,
    val ciudad: String,
    val categoria: CategoriaDto,
    val fechaTentativa: String
) {
    fun toDomain() = Lugar(
        id = id,
        viajeId = viajeId,
        nombre = nombre,
        descripcion = descripcion,
        ciudad = ciudad,
        categoria = categoria.toDomain(),
        fechaTentativa = LocalDate.parse(fechaTentativa)
    )

    companion object {
        fun fromDomain(lugar: Lugar) = LugarDto(
            id = lugar.id,
            viajeId = lugar.viajeId,
            nombre = lugar.nombre,
            descripcion = lugar.descripcion,
            ciudad = lugar.ciudad,
            categoria = CategoriaDto(lugar.categoria.id, lugar.categoria.nombre),
            fechaTentativa = lugar.fechaTentativa.toString()
        )
    }
}

data class ViajeDto(
    val id: Int = 0,
    val nombre: String,
    val destino: String,
    val fechaInicio: String,
    val fechaFin: String,
    val lugares: List<LugarDto> = emptyList()
) {
    fun toDomain() = Viaje(
        id = id,
        nombre = nombre,
        destino = destino,
        fechaInicio = LocalDate.parse(fechaInicio),
        fechaFin = LocalDate.parse(fechaFin),
        lugares = lugares.map { it.toDomain() }
    )

    companion object {
        fun fromDomain(viaje: Viaje) = ViajeDto(
            id = viaje.id,
            nombre = viaje.nombre,
            destino = viaje.destino,
            fechaInicio = viaje.fechaInicio.toString(),
            fechaFin = viaje.fechaFin.toString(),
            lugares = viaje.lugares.map { LugarDto.fromDomain(it) }
        )
    }
}

interface ViajesApiService {
    @GET("viajes")
    suspend fun getViajes(): List<ViajeDto>

    @GET("viajes/{id}")
    suspend fun getViajeDetalle(@Path("id") id: Int): ViajeDto

    @POST("viajes")
    suspend fun postViaje(@Body viaje: ViajeDto): ViajeDto

    @POST("viajes/{id}/lugares")
    suspend fun postLugar(@Path("id") viajeId: Int, @Body lugar: LugarDto): LugarDto

    @GET("categorias")
    suspend fun getCategorias(): List<CategoriaDto>
}

object RetrofitClient {
    private const val BASE_URL = "http://10.0.2.2:8080/"

    val apiService: ViajesApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ViajesApiService::class.java)
    }
}

class viajeRepository(
    private val apiService: ViajesApiService = RetrofitClient.apiService
) : ViajeRepository {

    override suspend fun obtenerViajes(): Result<List<Viaje>> = runCatching {
        apiService.getViajes().map { it.toDomain() }
    }

    override suspend fun obtenerDetalleViaje(viajeId: Int): Result<Viaje> = runCatching {
        apiService.getViajeDetalle(viajeId).toDomain()
    }

    override suspend fun crearViaje(viaje: Viaje): Result<Viaje> = runCatching {
        apiService.postViaje(ViajeDto.fromDomain(viaje)).toDomain()
    }

    override suspend fun agregarLugar(viajeId: Int, lugar: Lugar): Result<Lugar> = runCatching {
        apiService.postLugar(viajeId, LugarDto.fromDomain(lugar)).toDomain()
    }

    override suspend fun obtenerCategorias(): Result<List<Categoria>> = runCatching {
        apiService.getCategorias().map { it.toDomain() }
    }
}