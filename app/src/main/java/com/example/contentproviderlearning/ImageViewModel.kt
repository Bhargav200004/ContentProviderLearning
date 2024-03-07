package com.example.contentproviderlearning

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class ImageViewModel : ViewModel() {

    var image by mutableStateOf(emptyList<Image>())
        private set

    fun update(image : List<Image>){
        this.image = image
    }


}