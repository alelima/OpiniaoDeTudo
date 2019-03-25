package com.androiddesenv.opiniaodetudo.persistence

import android.arch.persistence.room.*
import com.androiddesenv.opiniaodetudo.model.Review

@Dao
interface ReviewDao {
    @Insert
    fun save(review: Review)

    @Query("SELECT * from ${ReviewTableInfo.TABLE_NAME}")
    fun listAll(): List<Review>

    @Delete
    fun delete(item: Review)

    @Update
    fun update(item: Review)

}