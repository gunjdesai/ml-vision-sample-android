package com.example.ocr

import android.content.Intent

import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView

import androidx.appcompat.app.AppCompatActivity
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition


class MainActivity : AppCompatActivity() {

    lateinit var imageViewer: ImageView
    lateinit var button: Button
    lateinit var outputText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageViewer = findViewById(R.id.image_viewer)
        button = findViewById(R.id.select_picture)
        outputText = findViewById(R.id.output_text)

        button.setOnClickListener {
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery, 100)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK && requestCode == 100) {
            val imageUri = data?.data
            val fbImage = InputImage.fromBitmap(MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri), 0)
            val recognizer = TextRecognition.getClient()

            imageViewer.setImageURI(imageUri)
            outputText.text = "Starting"
            val result = recognizer.process(fbImage)
                    .addOnSuccessListener {
                        var output = ""
                        val l1 = "->  \n"
                        val l2 = "->-> "

                        for (blocks in it.textBlocks) {
                            output += l1
                            for (line in blocks.lines) {
                                output += l2 + line.text
                            }
                        }
                        outputText.text = output
                    }
                .addOnFailureListener {

                    outputText.text = if (it.message.isNullOrEmpty()) "No error printed" else it.message
                }
        }
    }

}