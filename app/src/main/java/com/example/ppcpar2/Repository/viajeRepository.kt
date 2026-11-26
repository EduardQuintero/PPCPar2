package com.example.ppcpar2.Repository

import com.example.ppcpar2.Models.Categoria
import com.example.ppcpar2.Models.Lugar
import com.example.ppcpar2.Models.Viaje
import com.google.gson.annotations.SerializedName
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import java.time.LocalDate

interface ViajeRepository {
    suspend fun obtenerViajes(): Result<List<Viaje>>
    suspend fun obtenerDetalleViaje(viajeId: String): Result<Viaje>
    suspend fun crearViaje(viaje: Viaje): Result<Viaje>
    suspend fun agregarLugar(viajeId: String, lugar: Lugar): Result<Lugar>
    suspend fun obtenerCategorias(): Result<List<Categoria>>
}

data class CategoriaDto(
    val id: Int,
    @SerializedName("name") val nombre: String,
    @SerializedName("description") val descripcion: String = ""
) {
    fun toDomain() = Categoria(id = id, nombre = nombre)
}

data class LugarDto(
    val id: String = "",
    @SerializedName("tripId") val viajeId: String = "",
    @SerializedName("name") val nombre: String,
    @SerializedName("description") val descripcion: String,
    @SerializedName("city") val ciudad: String,
    @SerializedName("category") val categoria: CategoriaDto? = null,
    @SerializedName("categoryId") val categoriaId: Int = 0,
    @SerializedName("tentativeDate") val fechaTentativa: String
) {
    fun toDomain() = Lugar(
        id = id,
        viajeId = viajeId,
        nombre = nombre,
        descripcion = descripcion,
        ciudad = ciudad,
        categoria = categoria?.toDomain() ?: Categoria(id = categoriaId, nombre = ""),
        fechaTentativa = LocalDate.parse(fechaTentativa)
    )

    companion object {
        fun fromDomain(lugar: Lugar) = LugarDto(
            id = lugar.id,
            viajeId = lugar.viajeId,
            nombre = lugar.nombre,
            descripcion = lugar.descripcion,
            ciudad = lugar.ciudad,
            categoriaId = lugar.categoria.id,
            fechaTentativa = lugar.fechaTentativa.toString()
        )
    }
}

data class ViajeDto(
    val id: String = "",
    @SerializedName("name") val nombre: String,
    @SerializedName("destination") val destino: String,
    @SerializedName("startDate") val fechaInicio: String,
    @SerializedName("endDate") val fechaFin: String,
    @SerializedName("places") val lugares: List<LugarDto> = emptyList()
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
    @GET("trips")
    suspend fun getViajes(): List<ViajeDto>

    @GET("trips/{id}")
    suspend fun getViajeDetalle(@Path("id") id: String): ViajeDto

    @POST("trips")
    suspend fun postViaje(@Body viaje: ViajeDto): ViajeDto

    @POST("places")
    suspend fun postLugar(@Body lugar: LugarDto): LugarDto

    @GET("categories")
    suspend fun getCategorias(): List<CategoriaDto>
}

object RetrofitClient {
    private const val BASE_URL = "http://10.0.2.2:8000/api/"

    val apiService: ViajesApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ViajesApiService::class.java)
    }
}

class ViajeRepositoryImpl(
    private val apiService: ViajesApiService = RetrofitClient.apiService
) : ViajeRepository {

    override suspend fun obtenerViajes(): Result<List<Viaje>> = runCatching {
        apiService.getViajes().map { it.toDomain() }
    }

    override suspend fun obtenerDetalleViaje(viajeId: String): Result<Viaje> = runCatching {
        apiService.getViajeDetalle(viajeId).toDomain()
    }

    override suspend fun crearViaje(viaje: Viaje): Result<Viaje> = runCatching {
        apiService.postViaje(ViajeDto.fromDomain(viaje)).toDomain()
    }

    override suspend fun agregarLugar(viajeId: String, lugar: Lugar): Result<Lugar> = runCatching {
        apiService.postLugar(LugarDto.fromDomain(lugar.copy(viajeId = viajeId))).toDomain()
    }

    override suspend fun obtenerCategorias(): Result<List<Categoria>> = runCatching {
        apiService.getCategorias().map { it.toDomain() }
    }
}
