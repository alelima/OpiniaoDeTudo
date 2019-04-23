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

    @Query("DELETE FROM ${ReviewTableInfo.TABLE_NAME} WHERE ${ReviewTableInfo.COLUMN_ID} = :id")
    fun delete(id: String?)

    @Update
    fun update(item: Review)

}