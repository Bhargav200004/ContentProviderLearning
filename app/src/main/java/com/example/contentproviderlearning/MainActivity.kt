package com.example.contentproviderlearning

import android.Manifest
import android.content.ContentUris
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import coil.compose.AsyncImage
import com.example.contentproviderlearning.ui.theme.ContentProviderLearningTheme
import java.util.Calendar

class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<ImageViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_MEDIA_IMAGES),
            0
        )

        val millisYesterday = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR,-5)
        }.timeInMillis

        // project or give only id and display from the media
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME
        )

        //when we want to select media ------> date
        val selection = "${MediaStore.Images.Media.DATE_TAKEN} >= ?"

        val selectionArgs = arrayOf(
            millisYesterday.toString()
        )

        //sort the date according to descending order
        val sortOrder = "${MediaStore.Images.Media.DATE_TAKEN} DESC"


        contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )?.use { cursor->

            val idColumn = cursor.getColumnIndex(MediaStore.Images.Media._ID)
            val nameColumn = cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME)

            val image = mutableListOf<Image>()
            while (cursor.moveToNext()){
                val id = cursor.getLong(idColumn)
                val name = cursor.getString(nameColumn)
                val uri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id
                )

                image.add(Image(id,name,uri))
            }
            viewModel.update(image)
        }


        setContent {
            ContentProviderLearningTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                   // Log.i("HELOOO","${viewModel.image.get(1).uri} , ${viewModel.image.get(1).imageName}")
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        items(viewModel.image){image->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                AsyncImage(
                                    model = image.uri,
                                    contentDescription = null
                                )
                                Text(text = image.imageName)

                                Log.i("HELOOO","${image.uri} , ${image.imageName}")
                            }
                        }

                    }

                }
            }
        }
    }
}

data class Image (
    val imageId : Long,
    val imageName : String,
    val uri : Uri
)