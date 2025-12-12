package com.example.appnebula.data.repository

import android.content.Context
import com.example.appnebula.model.reserva.ReservaDto
import com.example.appnebula.model.reserva.ReservaResponse
import com.example.appnebula.network.ApiClient
import com.example.appnebula.network.ApiResponse
import retrofit2.Response

class ReservaRepository(private val context: Context) {

    private val apiService = ApiClient.getInstance(context)

    suspend fun crearReserva(reservaDto: ReservaDto): Response<ApiResponse<ReservaResponse>> {
        return apiService.crearReserva(reservaDto)
    }
}
