package com.androiddesenv.opiniaodetudo

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.androiddesenv.opiniaodetudo.model.repository.ReviewRepository

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var button = findViewById<Button>(R.id.button_save)
        var texViewName = findViewById<TextView>(R.id.input_nome)
        var texViewReview = findViewById<TextView>(R.id.input_review)

        //Esconder o teclado
        val mainContainer = findViewById<ConstraintLayout>(R.id.main_container)
        mainContainer.setOnTouchListener { v, event ->
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }

        button.setOnClickListener {
            var name = texViewName.text.toString()
            var review = texViewReview.text.toString()
            //Toast.makeText(this, "Nome: $name - Opinião: $review", Toast.LENGTH_LONG).show()
            object: AsyncTask<Void, Void, Unit>() {
                override fun doInBackground(vararg params: Void?) {
                    val repository = ReviewRepository(this@MainActivity.applicationContext)
                    repository.save(name.toString(), review.toString())
                    startActivity(Intent(this@MainActivity, ListActivity::class.java))
                }
            }.execute()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item?.itemId == R.id.menu_list_reviews){
            startActivity(Intent(this, ListActivity::class.java))
            return true
        }
        return false
    }
}
