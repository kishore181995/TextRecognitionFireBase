package com.example.androidtask26nov2020

import android.Manifest.permission.CAMERA
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.firebase.FirebaseApp
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionText
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity()
{
    lateinit var ocrImage: ImageView
    lateinit var capture: TextView
    lateinit var extract: TextView
    lateinit var resultEditText: EditText
    lateinit var progress : ProgressDialog

    lateinit var button : AppCompatButton
    lateinit var texView : AppCompatTextView

    var lists = listOf<String>("C","A","B")

    var number = 10;

    val numbers : IntArray = intArrayOf(1,2,3,4,5,6,78,89)

    //permission codes
    val REQUEST_IMAGE_CAPTURE = 2
    private val PERMISSION_REQUEST_CODE: Int = 101

    var dataStr = "";

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        progress = ProgressDialog(this@MainActivity)
        progress.setTitle("Recognising text ")

        resultEditText = findViewById(R.id.ocrResultEt)
        ocrImage = findViewById(R.id.imageView)
        capture = findViewById(R.id.capture)
        extract = findViewById(R.id.extract)
        extract.visibility = View.GONE
        resultEditText.visibility = View.GONE


//        fun main(args: ArrayList<String>)
//        {
//            Log.d("IN LOOF","CHECK")
            var text = "KISHORE"
            for (letter in text)
            {
                Log.d("KEY", letter.toString());
            }
//        }

        //odd numbers
            for (num in numbers ) {
//                if (i%2!=0)
                Log.d("CHECK", "$num")
            }

        var str ="KIShORE"

        var revers=str.reversed()
//        for (i in str.length-1 downTo 0)
//        {
//            revers+=str[i]
//        }

        number =121
        var reverenum =0
        var remainder =0
        while (number!=0)
        {
            remainder = number%10;
            reverenum = reverenum*10+remainder;
            number = number/10
        }

        val n =10
        var t1 = 0
        var t2 = 1

        for (i in 1..n)
        {
            Log.d("FIB-->",""+t1);

            val sum =t1+t2
            t1=t2
            t2 =sum
        }
        Log.d("REVERSE",revers);




        capture.setOnClickListener{
            //  popup for user to choose any one of the following
            popup()
        }



    }


    // menu selection pop - up dialog box
    @RequiresApi(Build.VERSION_CODES.O)
    private fun popup() {
        val alertDialogBuilder =
            AlertDialog.Builder(
                this
            )
        val inflater =
            getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        alertDialogBuilder.setView(R.layout.popup_layout)
        alertDialogBuilder.setCancelable(false)
        val dialog = alertDialogBuilder.create()
        dialog.show()
        val camera: AppCompatTextView?
        val galery: AppCompatTextView?
        val cancel: AppCompatTextView?
        camera = dialog.findViewById(R.id.cameraPopup)
        cancel = dialog.findViewById(R.id.exitPopup)
        galery = dialog.findViewById(R.id.galleryPopup)

        val numbers: IntArray = intArrayOf(0,2,3,4,5,6)

//        val text: ArrayList<String> = listOf<String>("","","")


        camera?.setOnClickListener{
            if (checkPersmission()) takePicture() else requestPermission()
            dialog.dismiss()
            resultEditText.visibility = View.GONE

        }

        galery?.setOnClickListener{
            pickImage()
            dialog.dismiss()
            resultEditText.visibility = View.GONE

        }

        cancel?.setOnClickListener{
            dialog.dismiss()
        }
    }

    // gallery picker
    fun pickImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1)

    }

    // on result code after picture capture / selection
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            progress.show();
            ocrImage.setImageURI(data!!.data)
            dataStr = data!!.data.toString()
            processImage(capture)
        }
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            progress.show();
            var bitmap: Bitmap = BitmapFactory.decodeFile(dataStr)
            ocrImage.setImageBitmap(bitmap)
            processImage(capture)
        }
    }

    // Firebase for text recognition
    fun processImage(v: View) {
        FirebaseApp.initializeApp(this)
        if (ocrImage.drawable != null) {
            resultEditText.setText("")
            v.isEnabled = false
            val bitmap = (ocrImage.drawable as BitmapDrawable).bitmap
            val image = FirebaseVisionImage.fromBitmap(bitmap)
            val detector = FirebaseVision.getInstance().onDeviceTextRecognizer

            detector.processImage(image)
                .addOnSuccessListener { firebaseVisionText ->
                    v.isEnabled = true
                    processResultText(firebaseVisionText)
                }
                .addOnFailureListener {
                    v.isEnabled = true
                    progress.dismiss()
                    resultEditText.visibility = View.VISIBLE
                    resultEditText.setText("Failed")
                }
        } else {
            Toast.makeText(this, "Select an Image First", Toast.LENGTH_LONG).show()
        }

    }

    //persmission check codes
    private fun checkPersmission(): Boolean {
        return (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,
            android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
    }
    private fun requestPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(READ_EXTERNAL_STORAGE, CAMERA),
            PERMISSION_REQUEST_CODE)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    &&grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    takePicture()
                } else {
                    Toast.makeText(this,"Permission Denied",Toast.LENGTH_SHORT).show()
                }
                return
            }
            else -> {
            }
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun takePicture() {

        val intent: Intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val file: File = createFile()
        val uri: Uri = FileProvider.getUriForFile(
            this,
            "com.example.androidtask26nov2020",
            file
        )
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
    }
    @Throws(IOException::class)
    private fun createFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = this!!.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            dataStr = absolutePath
        }
    }

    // Convert the blocks data to text
    private fun processResultText(resultText: FirebaseVisionText) {
        Log.d("CHECK","LOG");
        if (resultText.textBlocks.size == 0) {
            resultEditText.visibility = View.VISIBLE
            resultEditText.setText("No Text Found")
            Log.d("CHECK","NOTEXT");
            progress.dismiss()
            return
        }
        for (block in resultText.textBlocks) {
            val blockText = block.text
            resultEditText.append(blockText + "\n")
        }
        progress.dismiss()
        val intent = Intent(this,
            TextDisplayActivity::class.java)
        intent.putExtra("text",resultText.text)
        intent.putExtra("pic",dataStr);
        startActivity(intent)
    }
}