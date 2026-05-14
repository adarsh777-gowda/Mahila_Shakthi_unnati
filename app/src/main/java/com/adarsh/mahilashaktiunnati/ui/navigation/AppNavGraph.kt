package com.adarsh.mahilashaktiunnati.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.adarsh.mahilashaktiunnati.ui.screens.*
import com.adarsh.mahilashaktiunnati.viewmodel.AIViewModel
import com.adarsh.mahilashaktiunnati.viewmodel.AuthViewModel
import com.adarsh.mahilashaktiunnati.viewmodel.MemberViewModel

@Composable
fun AppNavGraph(
    isLoggedIn: Boolean,
    authViewModel: AuthViewModel,
    memberViewModel: MemberViewModel,
    isOffline: Boolean = false,
    onLogout: () -> Unit = {}
) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val aiViewModel = androidx.lifecycle.viewmodel.compose.viewModel<AIViewModel>()
    val start = if (isLoggedIn) Routes.Home else Routes.Login

    NavHost(navController = navController, startDestination = start) {
        composable(Routes.Login) {
            ProfessionalLoginScreen(
                context = context,
                viewModel = authViewModel,
                onLoginSuccess = {
                    navController.navigate(Routes.Home) {
                        popUpTo(Routes.Login) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Routes.Register)
                }
            )
        }

        composable(Routes.Register) {
            ProfessionalRegisterScreen(
                context = context,
                viewModel = authViewModel,
                onRegisterSuccess = {
                    navController.navigate(Routes.Home) {
                        popUpTo(Routes.Register) { inclusive = true }
                    }
                },
                onLoginSuccess = {
                    navController.navigate(Routes.Home) {
                        popUpTo(Routes.Register) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.Report) {
            ReportScreen(
                context = context,
                onBack = {
                    navController.navigateUp()
                }
            )
        }

        composable(Routes.Help) {
            HelpScreen(
                context = context,
                onBack = {
                    navController.navigateUp()
                }
            )
        }

        composable(Routes.Home) {
            ProfessionalHomeScreen(
                context = context,
                viewModel = memberViewModel,
                onNavigateToMembers = {
                    navController.navigate(Routes.Dashboard)
                },
                onNavigateToSavings = {
                    navController.navigate(Routes.Dashboard)
                },
                onNavigateToLoans = {
                    navController.navigate(Routes.Dashboard)
                },
                onNavigateToAI = {
                    navController.navigate(Routes.AIAssistant)
                },
                onNavigateToPracticalFeatures = {
                    navController.navigate(Routes.PracticalFeatures)
                },
                onNavigateToAdvancedFeatures = {
                    navController.navigate(Routes.Dashboard)
                },
                onNavigateToReport = {
                    navController.navigate(Routes.Report)
                },
                onNavigateToHelp = {
                    navController.navigate(Routes.Help)
                },
                onLogout = { 
                    authViewModel.signOut()
                    onLogout()
                    navController.navigate(Routes.Login) {
                        popUpTo(Routes.Home) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.Dashboard) {
            DashboardScreen(
                context = context,
                viewModel = memberViewModel,
                isOffline = isOffline,
                onMemberSelected = { memberId ->
                    navController.navigate("${Routes.MemberDetail}/$memberId")
                },
                onLogout = {
                    authViewModel.signOut()
                    onLogout()
                    navController.navigate(Routes.Login) {
                        popUpTo(Routes.Dashboard) { inclusive = true }
                    }
                },
                onNavigateToAI = {
                    navController.navigate(Routes.AIAssistant)
                }
            )
        }

        composable(
            route = "${Routes.MemberDetail}/{memberId}",
            arguments = listOf(navArgument("memberId") { type = NavType.IntType })
        ) { backStackEntry ->
            val memberId = backStackEntry.arguments?.getInt("memberId") ?: return@composable
            MemberDetailScreen(
                context = context,
                memberId = memberId,
                viewModel = memberViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.AIAssistant) {
            AIAssistantScreen(
                context = context,
                aiViewModel = aiViewModel,
                memberViewModel = memberViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.PracticalFeatures) {
            PracticalFeaturesScreen(
                context = context,
                viewModel = memberViewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
