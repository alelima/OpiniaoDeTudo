package com.androiddesenv.opiniaodetudo

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.androiddesenv.opiniaodetudo.model.Review
import com.androiddesenv.opiniaodetudo.model.repository.ReviewRepository
import com.androiddesenv.opiniaodetudo.online.BASE_URL
import com.androiddesenv.opiniaodetudo.online.REVIEWS_URI
import com.androiddesenv.opiniaodetudo.viewmodel.EditReviewViewModel
import okhttp3.*
import org.json.JSONObject
import java.io.File

class ListFragment : Fragment() {
    private lateinit var reviews: MutableList<Review>
    private lateinit var rootView: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.list_review_layout, null)

        val listView = rootView.findViewById<ListView>(R.id.list_recyclerview)
        initList(listView)
        configureOnClick(listView)
        configureOnLongClick(listView)
        configureListObserver()

        return rootView
    }

    private fun initList(listView: ListView) {
        @SuppressLint("StaticFieldLeak")
        val execute = object: AsyncTask<Void, Void, ArrayAdapter<Review>>() {
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

                        //Log.d("TESTE", item.thumbnail.toString())
                        if(item.thumbnail != null){
                            val thumbnail = itemView.findViewById<ImageView>(R.id.thumbnail)
                            val bitmap = BitmapFactory.decodeByteArray(item.thumbnail, 0, item.thumbnail.size)
                            thumbnail.setImageBitmap(bitmap)
                        }
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
                    R.id.item_list_map -> openMap(reviews[position])
                    R.id.item_list_upload -> uploadItem(reviews[position])
                }
                true
            }

            reviews[position].apply{
                if(latitude != null && longitude != null){
                    val item = popupMenu.menu.findItem(R.id.item_list_map)
                    item.isVisible = true
                }
            }
            popupMenu.show()
            true
        }
    }

    private fun openItemForEdition(item: Review) {
        val reviewViewModel = ViewModelProviders.of(activity!!).get(EditReviewViewModel::class.java)
        val data = reviewViewModel.data
        data.value = item
        EditDialogFragment().show(fragmentManager, "edit_dialog")
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

    private fun configureListObserver() {
        val reviewViewModel = ViewModelProviders.of(activity!!).get(EditReviewViewModel::class.java)
        reviewViewModel.data.observe(this, Observer {
            onResume()
        })
    }

    private fun openMap(review: Review) {
        val uri = Uri.parse("geo:${review.latitude},${review.longitude}")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        activity!!.startActivity(intent)
    }

    private fun configureOnClick(listView: ListView) {
        listView.setOnItemClickListener { parent, view, position, id ->
            val reviewViewModel =
                ViewModelProviders.of(activity!!).get(EditReviewViewModel::class.java)
            val data = reviewViewModel.data
            data.value = reviews[position]
            (activity!! as MainActivity).navigateWithBackStack(ShowReviewFragment())
        }
    }

    private fun uploadPhoto(idOnline: String,review: Review,client: OkHttpClient) {
        try{
            val fieRequestBody = RequestBody
                .create(
                    MediaType.get("image/jpg"),
                    File(activity!!.filesDir, review.photoPath)
                )
            val multipartBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("image", idOnline, fieRequestBody)
                .build()
            val request = Request.Builder()
                .url("$BASE_URL/$REVIEWS_URI/$idOnline/photo")
                .post(multipartBody)
                .build()
            client.newCall(request).execute()
        }catch (e:Exception){
            Log.e("ERROR", "Erro", e)
            Snackbar
                .make(
                    rootView,
                    "Erro ao enviar foto da opinião",
                    Snackbar.LENGTH_INDEFINITE)
                .setAction("Ok", {})
                .show()
        }
    }

    private fun uploadItem(review: Review) {
        object : AsyncTask<Void, Void, Unit>(){
            override fun doInBackground(vararg params: Void?) {
                try{
                    val jsonObject = JSONObject().apply {
                        put("id", review.id)
                        put("name", review.name)
                        put("review", review.review)
                        put("latitude", review.latitude)
                        put("longitude", review.longitude)
                        put("thumbnail", review.thumbnail?.toBase64())
                    }
                    val httpClient = OkHttpClient()
                    val body = RequestBody
                        .create(
                            MediaType.get("application/json"),
                            jsonObject.toString()
                        )
                    val request = Request.Builder()
                        .url("$BASE_URL/$REVIEWS_URI")
                        .post(body)
                        .build()
                    val response = httpClient.newCall(request).execute()
                    Snackbar
                        .make(
                            rootView,
                            "Opinião Enviada com Sucesso!",
                            Snackbar.LENGTH_LONG)
                        .show()
                    val jsonReponse = JSONObject(response.body()!!.string())
                    if(review.photoPath != null) {
                        uploadPhoto(jsonReponse.getString("id"), review, httpClient)
                    }
                }catch (e:Exception){
                    Log.e("ERROR", "Erro", e)
                    Snackbar
                        .make(
                            rootView,
                            "Erro ao enviar opinião",
                            Snackbar.LENGTH_INDEFINITE)
                        .setAction("Ok", {})
                        .show()
                }
            }
        }.execute()
    }
}

private fun ByteArray?.toBase64(): String {
    return String(Base64.encode(this, Base64.DEFAULT))
}