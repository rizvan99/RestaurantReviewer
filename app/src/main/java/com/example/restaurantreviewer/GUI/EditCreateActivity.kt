package com.example.restaurantreviewer.GUI

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.InputType
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Observer
import com.example.restaurantreviewer.Database.Room.RestaurantRepository
import com.example.restaurantreviewer.Database.Room.observeOnce
import com.example.restaurantreviewer.Model.Review
import com.example.restaurantreviewer.R
import kotlinx.android.synthetic.main.activity_editcreate.*
import java.io.File
import java.util.*

class EditCreateActivity : AppCompatActivity() {

    private lateinit var review: Review
    private lateinit var restRepo: RestaurantRepository
    private var pictureFile: File? = null
    private val TAG = "EditCreateActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editcreate)

        setupRatingsSpinner()
        setupEditReviewText()

        restRepo = RestaurantRepository.get()

        if (intent.extras != null) {
            val extras: Bundle = intent.extras!!
            review = extras.getSerializable(getString(R.string.REVIEW_INTENT)) as Review
            restRepo.getRestaurantById(review.restaurantId).observeOnce(
                this,
                Observer { restaurant ->
                    tvRestaurantInfo.text = getString(R.string.restaurantInfo, restaurant.name)
                }
            )
            restRepo.getUserById(review.userId).observeOnce(
                this,
                Observer { user ->
                    tvReviewerInfo.text = getString(R.string.reviewerInfo, user.name)
                }
            )

            // Insert information from review in fields (If it isn't a new review)
            if (review.id != null) {
                setImageFromFileString(imgReview, review.picture)
                spRating.setSelection(review.rating-1)
                if (review.review.isBlank()) {
                    etReview.setHint("No detailed review written yet...")
                } else {
                    etReview.setText(review.review)
                }
            }
            countCharacters(etReview.text.toString())

        } else {
            val errorMsg = "Intent is empty"
            failure(errorMsg)
        }
    }

    fun onClickSaveReview(view: View) {
        review.review = etReview.getText().toString().trim()
        if (spRating.selectedItem == null) {
            Toast.makeText(this, "You must select a rating", Toast.LENGTH_LONG).show()
            return
        } else {
            review.rating = spRating.selectedItem.toString().toInt()
        }

        val intent = Intent()
        if (review.id == null) {
            review.date = Date()
            intent.putExtra(getString(R.string.REVIEW_INTENT), review)
            setResult(getString(R.string.RESULT_CREATED).toInt(), intent)
        } else {
            intent.putExtra(getString(R.string.REVIEW_INTENT), review)
            setResult(getString(R.string.RESULT_UPDATED).toInt(), intent)
        }
        Log.d(TAG, "Saving review:\nId: ${review.id}\nRating: ${review.rating}\nReview: ${review.review}\nUri: ${review.picture}")
        finish()
    }

    fun onClickGoBack(view: View) {
        setResult(getString(R.string.RESULT_NOSAVE).toInt())
        finish()
    }

    private fun failure(errorMsg: String) {
        Log.d(TAG, errorMsg)

        val intent = Intent()
        intent.putExtra(getString(R.string.ERROR_INTENT), errorMsg)
        setResult(getString(R.string.RESULT_FAILED).toInt())
        finish()
    }

    private fun setupRatingsSpinner() {
        spRating.adapter = ArrayAdapter.createFromResource(
            this,
            R.array.ratings,
            android.R.layout.simple_spinner_dropdown_item
        )
    }

    private fun setupEditReviewText() {
        etReview.imeOptions = EditorInfo.IME_ACTION_DONE
        etReview.setRawInputType(InputType.TYPE_CLASS_TEXT)
        etReview.doAfterTextChanged { text -> countCharacters(text.toString()) }
    }

    private fun countCharacters(text: String) {
        tvWordCount.text = getString(R.string.tvWordCount, text.length)

        if (text.length == 150) {
            tvWordCount.setTextColor(Color.RED)
        } else if (text.length >= 140) {
            tvWordCount.setTextColor(getColor(R.color.dark_orange))
        } else {
            tvWordCount.setTextColor(Color.BLACK)
        }
    }

    //region Camera
    fun onClickTakePicture(view: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val permissions = mutableListOf<String>()
            if (!hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            if (!hasPermission(Manifest.permission.CAMERA)) permissions.add(Manifest.permission.CAMERA)
            if (permissions.size > 0) {
                ActivityCompat.requestPermissions(this, permissions.toTypedArray(), getString(R.string.PERMISSION_REQUEST_CODE).toInt())
            } else {
                takePicture()
            }
        } else {
            takePicture()
        }
    }

    private fun hasPermission(permission: String): Boolean {
        return ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
    }

    private fun takePicture() {
        pictureFile = getOutputFile("RRCamera")
        if(pictureFile == null) {
            Toast.makeText(this, "Could not create file", Toast.LENGTH_LONG).show()
            return
        }
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val applicationId = "com.example.restaurantreviewer"
        intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(
            this, "${applicationId}.provider", pictureFile!!))
        try {
            startActivityForResult(intent, getString(R.string.CAMERA_REQUEST_CODE).toInt())
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "Camera activity not found!!!", Toast.LENGTH_LONG).show()
        }
    }

    private fun getOutputFile(folder: String): File? {
        val mediaStorageDir = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), folder)
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(TAG, "Failed to create picture directory")
                return null
            }
        }
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val postfix = "jpg"
        val prefix = "IMG"
        return File(mediaStorageDir.path + File.separator + prefix + "_" + timeStamp + "." + postfix)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == getString(R.string.CAMERA_REQUEST_CODE).toInt()) {
            if (resultCode == RESULT_OK) {
                val pictureString = Uri.fromFile(pictureFile!!).toString()
                review.picture = pictureString
                setImageFromFileString(imgReview, pictureString)
            }
            if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Cancelled taking picture", Toast.LENGTH_SHORT).show()
            }
        }
    }
    //endregion

    fun onClickRemovePicture(view: View) {
        review.picture = null
        setImageFromFileString(imgReview, null)
    }

    private fun setImageFromFileString(img: ImageView, fileString: String?) {
        if (fileString != null) {
            val uri = Uri.parse(fileString)
            img.setImageURI(uri)
        } else {
            img.setImageResource(R.drawable.addimage)
        }
        img.adjustViewBounds = true
        img.scaleType = ImageView.ScaleType.CENTER_INSIDE
    }
}