package ro.code4.monitorizarevot.ui.main

import android.os.Bundle
import androidx.core.view.GravityCompat
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.onNavDestinationSelected
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import ro.code4.monitorizarevot.BuildConfig
import ro.code4.monitorizarevot.R
import ro.code4.monitorizarevot.analytics.Event
import ro.code4.monitorizarevot.helper.*
import ro.code4.monitorizarevot.ui.base.BaseActivity
import ro.code4.monitorizarevot.ui.login.LoginActivity


class MainActivity : BaseActivity<MainViewModel>() {
    override val layout: Int
        get() = R.layout.activity_main

    private val firebaseAnalytics: FirebaseAnalytics by inject()
    override val viewModel: MainViewModel by viewModel()
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(toolbar)

        navController = findNavController(R.id.nav_host_fragment)


        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_forms,
                R.id.nav_guide,
                R.id.nav_about
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // This needs to be set after `setupWithNavController`
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu)

        navView.setCheckedItem(R.id.nav_forms)
        navView.menu.findItem(R.id.nav_safety).isVisible = viewModel.isSafetyItemVisible
        // Workaround to allow actions and navigation in the same component
        navView.setNavigationItemSelectedListener { item ->
            val handled = onNavDestinationSelected(item, navController)
            if (handled) {
                drawerLayout.closeDrawer(navView)
                true
            } else {
                when (item.itemId) {
                    R.id.nav_change_polling_station -> {
                        firebaseAnalytics.logEvent(Event.TAP_CHANGE_STATION.title, null)
                        viewModel.notifyChangeRequested()
                        changePollingStation()
                        true
                    }
                    R.id.nav_call -> {
                        firebaseAnalytics.logEvent(Event.TAP_CALL.title, null)
                        callSupportCenter()
                        true
                    }
                    R.id.nav_safety -> {
                        val result = browse(viewModel.safetyUrl, true)
                        if (!result) {
                            logW("No app to view ${viewModel.safetyUrl}")
                        }
                        true
                    }
                    else -> false
                }
            }
        }

        navLogout.setOnClickListener {
            viewModel.logout()
        }

        viewModel.onLogoutLiveData().observe(this, Observer {
            startActivityWithoutTrace(LoginActivity::class.java)
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    fun setTitle(title: String) {
        supportActionBar?.title = title
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}
