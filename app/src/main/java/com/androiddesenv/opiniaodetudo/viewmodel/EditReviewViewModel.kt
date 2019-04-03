package com.androiddesenv.opiniaodetudo.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import com.androiddesenv.opiniaodetudo.model.Review

class EditReviewViewModel : ViewModel() {
    var data: MutableLiveData<Review> = MutableLiveData()
}