package com.example.ppcpar2.Views

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ppcpar2.ViewModels.ViajeViewModel
import com.example.ppcpar2.ui.theme.PPCPar2Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PPCPar2Theme {
                val navController = rememberNavController()
                val viewModel: ViajeViewModel = viewModel()

                NavHost(navController = navController, startDestination = "viajes") {
                    composable("viajes") {
                        ViajesScreen(
                            viewModel = viewModel,
                            onNavigateToCrear = { navController.navigate("crear") },
                            onNavigateToDetalle = { viajeId -> navController.navigate("miviaje/$viajeId") }
                        )
                    }
                    composable("crear") {
                        CrearViajeScreen(
                            viewModel = viewModel,
                            onBack = { navController.popBackStack() },
                            onSuccess = { navController.popBackStack() }
                        )
                    }
                    composable(
                        route = "miviaje/{viajeId}",
                        arguments = listOf(navArgument("viajeId") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val viajeId = backStackEntry.arguments?.getString("viajeId") ?: ""
                        MiViajeScreen(
                            viajeId = viajeId,
                            viewModel = viewModel,
                            onBack = { navController.popBackStack() },
                            onNavigateToAddLugar = { id -> navController.navigate("agregar/$id") }
                        )
                    }
                    composable(
                        route = "agregar/{viajeId}",
                        arguments = listOf(navArgument("viajeId") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val viajeId = backStackEntry.arguments?.getString("viajeId") ?: ""
                        AgregarLugarScreen(
                            viajeId = viajeId,
                            viewModel = viewModel,
                            onBack = { navController.popBackStack() },
                            onSuccess = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}
