package com.example.util

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import android.widget.Toast
import androidx.core.content.FileProvider
import coil.Coil
import coil.request.ImageRequest
import coil.request.SuccessResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

object ShareHelper {

    private const val TAG = "GoalvixShare"

    /**
     * Shares the prediction image with friends using native Android share intent.
     * Fetches the image bitmap from Coil, writes to cache via FileProvider,
     * and presents the Android Chooser with an image and invite caption.
     */
    suspend fun sharePrediction(
        context: Context,
        imageUrl: String?,
        title: String = "Free Football Prediction",
        telegramUrl: String = "https://t.me/goalvix"
    ) = withContext(Dispatchers.IO) {
        val shareMessage = "⚽ Today's $title on GOALVIX!\nJoin our channel for daily picks: $telegramUrl"

        if (imageUrl.isNullOrBlank()) {
            shareText(context, shareMessage)
            return@withContext
        }

        try {
            val loader = Coil.imageLoader(context)
            val request = ImageRequest.Builder(context)
                .data(imageUrl)
                .allowHardware(false) // Required for bitmap software copying
                .build()

            val result = loader.execute(request)
            if (result is SuccessResult) {
                val drawable = result.drawable
                val bitmap = if (drawable is BitmapDrawable) {
                    drawable.bitmap
                } else {
                    val bmp = Bitmap.createBitmap(
                        drawable.intrinsicWidth.coerceAtLeast(1),
                        drawable.intrinsicHeight.coerceAtLeast(1),
                        Bitmap.Config.ARGB_8888
                    )
                    val canvas = android.graphics.Canvas(bmp)
                    drawable.setBounds(0, 0, canvas.width, canvas.height)
                    drawable.draw(canvas)
                    bmp
                }

                val cacheDir = File(context.cacheDir, "shared_predictions")
                if (!cacheDir.exists()) {
                    cacheDir.mkdirs()
                }

                val shareFile = File(cacheDir, "goalvix_free_prediction.jpg")
                FileOutputStream(shareFile).use { out ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 92, out)
                }

                val authority = "${context.packageName}.fileprovider"
                val contentUri = FileProvider.getUriForFile(context, authority, shareFile)

                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "image/jpeg"
                    putExtra(Intent.EXTRA_STREAM, contentUri)
                    putExtra(Intent.EXTRA_TEXT, shareMessage)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }

                val chooser = Intent.createChooser(intent, "Share Free Prediction with Friends").apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(chooser)
            } else {
                Log.w(TAG, "Coil could not fetch image for sharing, falling back to text")
                shareText(context, "$shareMessage\n$imageUrl")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error sharing image: ${e.message}", e)
            shareText(context, "$shareMessage\n$imageUrl")
        }
    }

    private fun shareText(context: Context, text: String) {
        try {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, text)
            }
            val chooser = Intent.createChooser(intent, "Share with Friends").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(chooser)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to launch text share", e)
            Toast.makeText(context, "Could not open share sheet", Toast.LENGTH_SHORT).show()
        }
    }
}
