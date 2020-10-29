package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {


    var btn_List: Button? = null
    var btn_Load: Button? = null
    var btn_take: Button? = null
    var massage: TextView? = null
    var ivphoto: ImageView? = null
    val REQUEST_TAKE_PHOTO = 1
    val SELECT_A_PHOTO = 2
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btn_take = findViewById<Button>(R.id.btn_take)
        btn_Load = findViewById<Button>(R.id.btn_Load)
        btn_List = findViewById(R.id.btn_List)
        ivphoto = findViewById(R.id.ivphoto)
        massage = findViewById(R.id.massage)
        btn_take?.setOnClickListener(View.OnClickListener { dispatchTakePictureIntent() })
        btn_Load?.setOnClickListener(View.OnClickListener {
            val i = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(i, SELECT_A_PHOTO)
        })
    }

    override protected fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //set the image in the ivphoto
        val ivphoto: ImageView
        ivphoto = findViewById(R.id.ivphoto)
        val massage: TextView
        massage = findViewById(R.id.massage)

        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            Glide.with(this).load(currentPhotoPath).into(ivphoto)
            massage.text = currentPhotoPath
        }
        if (requestCode == SELECT_A_PHOTO && resultCode == RESULT_OK) {
            val selectphoto = data?.data
            Glide.with(this).load(selectphoto).into(ivphoto)
            //show the File pass in text massage
            massage.text = selectphoto.toString()
        }
    }


    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            // Create the File where the photo should go
            var photoFile: File? = null
            try {
                photoFile = createImageFile()
            } catch (ex: IOException) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                val photoURI = FileProvider.getUriForFile(this,
                        "com.example.photoreq.fileprovider",
                        photoFile)
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
            }
        }
    }

    var currentPhotoPath: String? = null

    @Throws(IOException::class)
    private fun createImageFile(): File? {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",  /* suffix */
                storageDir /* directory */
        )

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.absolutePath
        return image
    }

}


