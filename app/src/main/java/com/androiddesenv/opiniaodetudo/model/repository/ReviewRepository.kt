package com.androiddesenv.opiniaodetudo.model.repository

import android.content.Context
import com.androiddesenv.opiniaodetudo.model.Review
import com.androiddesenv.opiniaodetudo.persistence.ReviewDao
import com.androiddesenv.opiniaodetudo.persistence.ReviewDatabase
import java.util.*

class ReviewRepository {
    private val reviewDao: ReviewDao

    constructor(context: Context){
        val reviewDatabase = ReviewDatabase.getInstance(context)
        reviewDao = reviewDatabase.reviewDao()
    }

    fun save(name: String, review: String) {
        reviewDao.save(Review(UUID.randomUUID().toString(), name, review))
    }
    fun listAll(): List<Review> {
        return reviewDao.listAll()
    }

    fun delete(item: Review) {
        return reviewDao.delete(item)
    }

    fun update(id: String, name: String, review: String) {
        reviewDao.update(Review(id, name, review))
    }
}