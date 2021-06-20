package com.ibrahim.istanbuledia

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_details.*
import java.io.ByteArrayOutputStream

class DetailsActivity : AppCompatActivity() {

    var selectedPicture : Uri? = null
    var selectedBitmap : Bitmap? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        val intent = intent
        val info = intent.getStringExtra("info")

        if (info == "new") {
            isimText.setText("")
            detayText.setText("")
            kaydetButonu.visibility = View.VISIBLE

            val selectedImageBackground = BitmapFactory.decodeResource(applicationContext.resources,R.drawable.secim)
            imageView.setImageBitmap(selectedImageBackground)

        }else{

            kaydetButonu.visibility = View.INVISIBLE
            val selectedId = intent.getIntExtra("id",1)


            val database = this.openOrCreateDatabase("Items", Context.MODE_PRIVATE,null)

            val cursor = database.rawQuery("SELECT * FROM items WHERE id = ?", arrayOf(selectedId.toString()))

            val itemNameIx = cursor.getColumnIndex("itemname")
            val detailsIx = cursor.getColumnIndex("details")
            val imageIx = cursor.getColumnIndex("image")

            while (cursor.moveToNext()) {
                isimText.setText(cursor.getString(itemNameIx))
                detayText.setText(cursor.getString(detailsIx))


                val byteArray = cursor.getBlob(imageIx)
                val bitmap = BitmapFactory.decodeByteArray(byteArray,0,byteArray.size)
                imageView.setImageBitmap(bitmap)
        }
            cursor.close()
        }
    }
    fun kaydet(view : View){

        val itemName = isimText.text.toString()
        val details = detayText.text.toString()

        if (selectedBitmap != null) {
            val smallBitmap = makeSmallerBitmap(selectedBitmap!!,300)

            val outputStream = ByteArrayOutputStream()
            smallBitmap.compress(Bitmap.CompressFormat.PNG,50,outputStream)
            val byteArray = outputStream.toByteArray()

            try {

                val database = this.openOrCreateDatabase("Items", Context.MODE_PRIVATE, null)
                database.execSQL("CREATE TABLE IF NOT EXISTS items (id INTEGER PRIMARY KEY, itemname VARCHAR, details VARCHAR, image BLOB)")

                val sqlString = "INSERT INTO items (itemname, details, image) VALUES (?, ?, ?)"
                val statement = database.compileStatement(sqlString)
                statement.bindString(1, itemName)
                statement.bindString(2, details)
                statement.bindBlob(3, byteArray)

                statement.execute()

            } catch (e: Exception) {
                e.printStackTrace()
            }


            val intent = Intent(this,MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

            startActivity(intent)



        }


    }


    fun makeSmallerBitmap(image: Bitmap, maximumSize : Int) : Bitmap {
        var width = image.width
        var height = image.height

        val bitmapRatio : Double = width.toDouble() / height.toDouble()
        if (bitmapRatio > 1) {
            width = maximumSize
            val scaledHeight = width / bitmapRatio
            height = scaledHeight.toInt()
        } else {
            height = maximumSize
            val scaledWidth = height * bitmapRatio
            width = scaledWidth.toInt()
        }

        return Bitmap.createScaledBitmap(image,width,height,true)

    }


    fun fotoSec(view: View){

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),1)
        } else {
            val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intentToGallery,2)
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 1) {

            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(intentToGallery,2)
            }

        }


        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if ( requestCode == 2 && resultCode == RESULT_OK && data != null) {

            selectedPicture = data.data

            try {

                if (selectedPicture != null) {

                    if (Build.VERSION.SDK_INT >= 28) {
                        val source = ImageDecoder.createSource(this.contentResolver, selectedPicture!!)
                        selectedBitmap = ImageDecoder.decodeBitmap(source)
                        imageView.setImageBitmap(selectedBitmap)
                    } else {
                        selectedBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, selectedPicture)
                        imageView.setImageBitmap(selectedBitmap)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()

            }

        }


        super.onActivityResult(requestCode, resultCode, data)
    }
}