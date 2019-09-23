package ro.code4.monitorizarevot.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.onNavDestinationSelected
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import org.koin.android.viewmodel.ext.android.viewModel
import ro.code4.monitorizarevot.R
import ro.code4.monitorizarevot.helper.callSupportCenter
import ro.code4.monitorizarevot.ui.base.BaseActivity
import ro.code4.monitorizarevot.ui.branch.BranchActivity

class MainActivity : BaseActivity<MainViewModel>() {
    override val layout: Int
        get() = R.layout.activity_main
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
                R.id.nav_change_branch,
                R.id.nav_guide
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        navView.setCheckedItem(R.id.nav_forms)
        //Workaround to allow actions and navigation in the same component
        navView.setNavigationItemSelectedListener { item ->
            val handled = onNavDestinationSelected(item, navController)
            if (handled) {
                drawerLayout.closeDrawer(navView)
                true
            } else {
                when (item.itemId) {
                    R.id.nav_change_branch -> {
                        startActivity(Intent(this, BranchActivity::class.java))
                        finishAffinity()
                        true
                    }
                    R.id.nav_call -> {
                        callSupportCenter()
                        true
                    }
                    R.id.nav_logout -> {
                        true

                    }
                    else -> false

                }
            }
        }

    }


    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
