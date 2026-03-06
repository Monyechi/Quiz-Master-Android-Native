package com.quizmaster.app.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.quizmaster.app.R
import com.quizmaster.app.databinding.ActivityMainBinding
import com.quizmaster.app.util.SessionManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    @Inject lateinit var session: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHost = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHost.navController

        val appBarConfig = AppBarConfiguration(
            setOf(
                R.id.loginFragment,
                R.id.studentDashboardFragment,
                R.id.instructorDashboardFragment
            )
        )
        setupActionBarWithNavController(navController, appBarConfig)

        // Route to correct start destination based on session
        val graph = navController.navInflater.inflate(R.navigation.nav_graph)
        graph.setStartDestination(
            when {
                !session.isLoggedIn -> R.id.loginFragment
                session.currentUserRole == "Instructor" -> R.id.instructorDashboardFragment
                else -> R.id.studentDashboardFragment
            }
        )
        navController.graph = graph
    }

    override fun onSupportNavigateUp(): Boolean {
        val navHost = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        return navHost.navController.navigateUp() || super.onSupportNavigateUp()
    }
}
