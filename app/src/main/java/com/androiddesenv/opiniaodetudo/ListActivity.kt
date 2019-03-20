package com.androiddesenv.opiniaodetudo

import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import com.androiddesenv.opiniaodetudo.model.Review
import com.androiddesenv.opiniaodetudo.model.repository.ReviewRepository

class ListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.list_review_layout)
        val listView = findViewById<ListView>(R.id.list_recyclerview)
        this.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        initList(listView)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun initList(listView: ListView) {
        object: AsyncTask<Void, Void, ArrayAdapter<Review>>() {
            override fun doInBackground(vararg params: Void?): ArrayAdapter<Review> {
                val reviews = ReviewRepository(this@ListActivity.applicationContext)
                    .listAll()
                val adapter = object : ArrayAdapter<Review>(this@ListActivity, -1, reviews ){
                    override fun getView(
                        position: Int,
                        convertView: View?,
                        parent: ViewGroup?): View {
                        val itemView = layoutInflater.inflate(R.layout.review_list_item_layout, null)
                        val item = reviews[position]
                        val textViewName = itemView
                            .findViewById<TextView>(R.id.item_name)
                        val textViewReview = itemView
                            .findViewById<TextView>(R.id.item_review)
                        textViewName.text = item.name
                        textViewReview.text = item.review
                        return itemView
                    }
                }
                return adapter
            }
            override fun onPostExecute(adapter: ArrayAdapter<Review>) {
                listView.adapter = adapter
            }
        }.execute()
    }
}
