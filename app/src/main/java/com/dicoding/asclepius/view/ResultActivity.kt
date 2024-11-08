package com.dicoding.asclepius.view

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.net.toFile
import com.dicoding.asclepius.R
import com.dicoding.asclepius.data.Prediction
import com.dicoding.asclepius.data.PredictionDatabase
import com.dicoding.asclepius.databinding.ActivityResultBinding
import com.dicoding.asclepius.helper.ImageClassifierHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.tensorflow.lite.task.vision.classifier.Classifications
import org.tensorflow.lite.task.vision.classifier.ImageClassifier
import java.io.File
import java.io.FileOutputStream

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding

    companion object {
        const val EXTRA_IMAGE_URI = "image_uri"
        const val TAG = "ResultActivity"
        const val RESULT_TEXT = "result_text"
        const val REQUEST_HISTORY_UPDATE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // TODO: Menampilkan hasil gambar, prediksi, dan confidence score.
        val imageUri = intent.getStringExtra(EXTRA_IMAGE_URI)
        if (imageUri != null) {
            val uri = Uri.parse(imageUri)
            displayImage(uri)

            val imageClassifier = ImageClassifierHelper(
                context = this,
                classifierListener = object : ImageClassifierHelper.ClassifierListener {
                    override fun onError(error: String) {
                        Log.d(TAG, "Error: $error")
                    }
                    override fun onResults(
                        results: List<Classifications>?,
                        inferenceTime: Long
                    ) {
                        results?.let {showResult(it)}
                    }
                }
            )
            imageClassifier.classifyStaticImage(uri)
        } else {
            Log.e(TAG, "No image URI provided")
            finish()
        }

        binding.btnSave.setOnClickListener {
            val resultText = binding.resultText.text.toString()
            val imageUriString = intent.getStringExtra(EXTRA_IMAGE_URI)

            if (imageUriString != null) {
                val imageUri = Uri.parse(imageUriString)
                showToast("Image saved successfully")
                savePrediction(imageUri, resultText)
            } else {
                showToast("No image URI provided")
                finish()
            }
        }
    }

    private fun savePrediction(imageUri: Uri?, resultText: String) {
        if (resultText.isNotEmpty()) {
            val file_name = "cropped_image_${System.currentTimeMillis()}.jpg"
            val destinationUri = Uri.fromFile(File(cacheDir, file_name))
            contentResolver.openInputStream(imageUri!!)?.use { inputStream ->
                FileOutputStream(File(cacheDir, file_name)).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            val prediction = Prediction(imagePath = destinationUri.toString(), predictionResult = resultText)
            GlobalScope.launch(Dispatchers.IO) {
                val db = PredictionDatabase.getInstance(this@ResultActivity)
                try {
                    db.predictionDao().insertPrediction(prediction)
                    Log.d(TAG, "Prediction saved successfully: $prediction")
                    val predictions = db.predictionDao().getAllPredictions()
                    Log.d(TAG, "All predictions: $predictions")
                    moveToHistory(destinationUri, resultText)
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to save prediction: $prediction", e)
                }
            }
        }
    }

    private fun moveToHistory(imageUri: Uri?, resultText: String) {
        val intent = Intent(this, HistoryActivity::class.java)
        intent.putExtra(EXTRA_IMAGE_URI, imageUri.toString())
        intent.putExtra(RESULT_TEXT, resultText)
        setResult(RESULT_OK, intent)
        startActivity(intent)
        finish()
    }

    private fun showToast(s: String) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show()
    }

    private fun displayImage(uri: Uri?) {
        Log.d(TAG, "Displaying image from URI: $uri")
        binding.resultImage.setImageURI(uri)
    }

    private fun showResult(it: List<Classifications>) {
        val topResult = it[0]
        val label = topResult.categories[0].label
        val score = topResult.categories[0].score

        fun Float.formatToString(): String {
            return String.format("%.2f%%", this * 100)
        }
        binding.resultText.text = "$label ${score.formatToString()}"
    }
}