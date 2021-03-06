package com.androiddesenv.opiniaodetudo

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.content.ContextCompat
import android.support.v7.preference.PreferenceManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.androiddesenv.opiniaodetudo.model.Review
import com.androiddesenv.opiniaodetudo.model.repository.ReviewRepository
import com.google.firebase.iid.FirebaseInstanceId

class MainActivity : AppCompatActivity() {

    private val fragments = mapOf(FORM_FRAGMENT to ::FormFragment, LIST_FRAGMENT to ::ListFragment,
        SETTINGS_FRAGMENT to ::SettingsFragment, ONLINE_FRAGMENT to ::OnlineFragment)
    companion object {
        val FORM_FRAGMENT = "formFragment"
        val LIST_FRAGMENT = "listFragment"
        const val SETTINGS_FRAGMENT = "settingsFragment"
        const val ONLINE_FRAGMENT = "onlineFragment"
        val GPS_PERMISSION_REQUEST = 101
        val PUSH_NOTIFICATION_MESSAGE_REQUEST = 1232
        val PUSH_NOTIFICATION_CHANNEL = "PushNotificationChannel"
        const val NEW_REVIEW_NOTIFICATION_MESSAGE_REQUEST = 1233
        const val DELETE_NOTIFICATION_ACTION_NAME = "DELETE"
        const val DELETE_NOTIFICATION_EXTRA_NAME = "REVIEW_TO_DELETE"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        chooseTheme()
        setContentView(R.layout.activity_main)
        if(savedInstanceState == null){
            navigateTo(FORM_FRAGMENT)
        }
        configureBottomMenu()
        configureAutoHiddenKeyboard()
        askForGPSPermission()
        logToken()
    }

    private fun logToken() {
        FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("TOKEN_FCM", task.exception)
            } else {
                val token = task.result?.token
                Log.d("TOKEN_FCM", "logToken:${token}")
            }
        }
    }

    private fun configureAutoHiddenKeyboard() {
        //Esconder o teclado
        val mainContainer = findViewById<ConstraintLayout>(R.id.main_container)
        mainContainer.setOnTouchListener { v, event ->
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
    }

    fun navigateTo(item: String) {
        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        val fragmentInstance: Fragment = fragments[item]?.invoke()!!
        supportFragmentManager
            .beginTransaction()
            .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out,
                android.R.anim.slide_in_left,
                android.R.anim.slide_out_right)
            .replace(R.id.fragment_container, fragmentInstance)
        .commit()
    }

    private fun configureBottomMenu() {
        val bottomNavigationMenu = findViewById<BottomNavigationView>(R.id.bottom_main_menu)
        bottomNavigationMenu.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.menuitem_newitem -> navigateTo(FORM_FRAGMENT)
                R.id.menuitem_listitem -> navigateTo(LIST_FRAGMENT)
                R.id.menuitem_settings -> navigateTo(SETTINGS_FRAGMENT)
                R.id.menuitem_online -> navigateTo(ONLINE_FRAGMENT)
            }
            true
        }
    }

    private fun askForGPSPermission() {
        if(ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                MainActivity.GPS_PERMISSION_REQUEST )
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            GPS_PERMISSION_REQUEST -> {
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this,"Permissão para usar o GPS concedida",
                        Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    private fun chooseTheme() {
        val nightMode = PreferenceManager.getDefaultSharedPreferences(this)
            .getBoolean(SettingsFragment.NIGHT_MODE_PREF, false)
        if(nightMode) {
            setTheme(R.style.AppThemeNight_NoActionBar)
        } else {
            setTheme(R.style.AppTheme_NoActionBar)
        }
    }

    fun setNightMode(){
        recreate()
    }

    override fun onSupportNavigateUp(): Boolean {
        supportFragmentManager.popBackStack()
        return true
    }
    fun navigateWithBackStack(destiny: Fragment) {
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                android.R.anim.fade_in,
                android.R.anim.fade_out,
                android.R.anim.slide_in_left,
                android.R.anim.slide_out_right)
            .replace(R.id.fragment_container, destiny)
            .addToBackStack(null)
            .commit()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        deleteReview(intent)
    }

    private fun deleteReview(intent: Intent?) {
        if(intent?.action == DELETE_NOTIFICATION_ACTION_NAME){
            val id = intent.getStringExtra(DELETE_NOTIFICATION_EXTRA_NAME)
            ReviewRepository(this.applicationContext).delete(id)
        }
    }

    /*
    private fun deleteReview(intent: Intent?) {
        val id = intent?.getStringExtra(DELETE_NOTIFICATION_EXTRA_NAME)
        if(intent?.action == DELETE_NOTIFICATION_ACTION_NAME){
            object : AsyncTask<Void, Void, Unit>() {
                override fun doInBackground(vararg params: Void?) {
                    ReviewRepository(this@MainActivity.applicationContext).delete(id)
                }
            }
        }
    }
     */
}
