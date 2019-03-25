package com.androiddesenv.opiniaodetudo

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.PopupMenu
import android.widget.TextView
import com.androiddesenv.opiniaodetudo.model.Review
import com.androiddesenv.opiniaodetudo.model.repository.ReviewRepository

class ListFragment : Fragment() {
    private lateinit var reviews: MutableList<Review>
    private lateinit var rootView: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.list_review_layout, null)
        val repo = ReviewRepository(activity!!.applicationContext)
        val listView = rootView.findViewById<ListView>(R.id.list_recyclerview)
        initList(listView)
        configureOnLongClick(listView)
        return rootView
    }

    private fun initList(listView: ListView) {
        object: AsyncTask<Void, Void, ArrayAdapter<Review>>() {
            override fun doInBackground(vararg params: Void?): ArrayAdapter<Review> {
                val repo = ReviewRepository(activity!!.applicationContext)
                reviews = repo.listAll().toMutableList()
                val adapter = object : ArrayAdapter<Review>(activity!!, -1, reviews ){
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

    private fun delete(item: Review) {
        object: AsyncTask<Unit, Void, Unit>(){
            override fun doInBackground(vararg params: Unit?) {
                ReviewRepository(activity!!.applicationContext).delete(item)
                reviews.remove(item)
            }
            override fun onPostExecute(result: Unit?) {
                val listView = activity!!.findViewById<ListView>(R.id.list_recyclerview)
                val adapter = listView.adapter as ArrayAdapter<Review>
                adapter.notifyDataSetChanged()
            }
        }.execute()
    }

    private fun configureOnLongClick(listView: ListView?) {
        listView?.setOnItemLongClickListener { _, view, position, _ ->
            val popupMenu = PopupMenu(activity!!, view)
            popupMenu.inflate(R.menu.list_review_item_menu)
            popupMenu.setOnMenuItemClickListener {
                when(it.itemId){
                    R.id.item_list_delete -> this.delete(reviews[position])
                    R.id.item_list_edit -> this.openItemForEdition(reviews[position])
                }
                true
            }
            popupMenu.show()
            true
        }
    }

    private fun openItemForEdition(item: Review) {
        val intent = Intent(activity!!, MainActivity::class.java)
        intent.putExtra("item", item)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        object : AsyncTask<Unit, Void, Unit>() {
            override fun doInBackground(vararg params: Unit?) {
                reviews.clear()
                reviews.addAll(ReviewRepository(activity!!.applicationContext).listAll())
            }
            override fun onPostExecute(result: Unit?) {
                val listView = rootView.findViewById<ListView>(R.id.list_recyclerview)
                val adapter = listView.adapter as ArrayAdapter<Review>
                adapter.notifyDataSetChanged()
            }
        }.execute()
    }
}