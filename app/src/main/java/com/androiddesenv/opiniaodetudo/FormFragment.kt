package com.androiddesenv.opiniaodetudo

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.androiddesenv.opiniaodetudo.model.Review
import com.androiddesenv.opiniaodetudo.model.repository.ReviewRepository

class FormFragment : Fragment(){
    private lateinit var mainView: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        mainView = inflater.inflate(R.layout.new_review_form_layout, null)
        val buttonSave = mainView.findViewById<Button>(R.id.button_save)
        val textViewName = mainView.findViewById<TextView>(R.id.input_nome)
        val textViewReview = mainView.findViewById<TextView>(R.id.input_review)
        val reviewToEdit = (activity!!.intent?.getSerializableExtra("item") as Review?)?.also { review ->
            textViewName.text = review.name
            textViewReview.text = review.review
        }
        buttonSave.setOnClickListener {
            val name = textViewName.text
            val review = textViewReview.text
            object : AsyncTask<Void, Void, Unit>() {
                override fun doInBackground(vararg params: Void?) {
                    val repository = ReviewRepository(activity!!.applicationContext)
                    if (reviewToEdit == null) {
                        repository.save(name.toString(), review.toString())
                        val i = Intent(activity!!.applicationContext, ListActivity::class.java)
                        startActivity(i)
                    } else {
                        repository.update(reviewToEdit.id, name.toString(), review.toString())
                        activity!!.finish()
                    }
                }
            }.execute()
            true
        }
        return mainView
    }

}