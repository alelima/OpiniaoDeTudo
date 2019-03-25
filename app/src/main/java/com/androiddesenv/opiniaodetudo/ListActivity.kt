package com.androiddesenv.opiniaodetudo

import android.content.Intent
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.PopupMenu
import android.widget.TextView
import com.androiddesenv.opiniaodetudo.model.Review
import com.androiddesenv.opiniaodetudo.model.repository.ReviewRepository

class ListActivity : AppCompatActivity() {

    private lateinit var reviews : MutableList<Review>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.list_review_activity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, ListFragment())
            .commit()
    }
}
