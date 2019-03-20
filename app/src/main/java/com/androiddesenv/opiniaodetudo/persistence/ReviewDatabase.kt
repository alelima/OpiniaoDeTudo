package com.androiddesenv.opiniaodetudo.persistence

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import com.androiddesenv.opiniaodetudo.model.Review

//Essa classe é só uma configuração, o ROM cria o database e retorna o DAO pra mim
@Database(entities = arrayOf(Review::class), version = 2)
abstract class ReviewDatabase : RoomDatabase(){
    companion object {
        private var instance: ReviewDatabase? = null
        fun getInstance(context: Context): ReviewDatabase {
            if(instance == null){
                instance = Room
                    .databaseBuilder(context, ReviewDatabase::class.java, "review_database")
                    .build()
            }
            return instance!!
        }
    }
    abstract fun reviewDao():ReviewDao
}