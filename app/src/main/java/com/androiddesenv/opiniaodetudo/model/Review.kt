package com.androiddesenv.opiniaodetudo.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity
data class Review(@PrimaryKey val id:String, val name:String, val review:String?)