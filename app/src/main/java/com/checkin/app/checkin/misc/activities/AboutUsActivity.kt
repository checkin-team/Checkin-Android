package com.checkin.app.checkin.misc.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import butterknife.BindView
import butterknife.ButterKnife
import com.checkin.app.checkin.R



class AboutUsActivity : AppCompatActivity() {

    @BindView(R.id.im_aboutus_googleplay)
    lateinit var imGooglePlay: ImageView

    @BindView(R.id.im_aboutus_contact)
    lateinit var imContact: ImageView

    @BindView(R.id.im_aboutus_facebook)
    lateinit var imFacebook: ImageView

    @BindView(R.id.im_aboutus_gmail)
    lateinit var imGmail: ImageView

    @BindView(R.id.im_aboutus_youtube)
    lateinit var imYoutube: ImageView

    @BindView(R.id.im_aboutus_instagram)
    lateinit var imInstagram: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_us)
        ButterKnife.bind(this)
        supportActionBar?.title = "About Us"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupImage()
    }


    private fun setupImage() {
        val dialogView = layoutInflater.inflate(R.layout.incl_contact_alert, null)
        val builder = AlertDialog.Builder(this)
                .setView(dialogView)
                .setTitle("Contact Us")

        val alertDialog = builder.create()

        alertDialog.setOnCancelListener {
            alertDialog.dismiss()
        }




        imGooglePlay.setOnClickListener {
            val appPackageName = packageName

            try {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName")))
            } catch (e: ActivityNotFoundException) {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")))
            }
        }

        imContact.setOnClickListener {
            alertDialog.show()

            val mail: TextView? = alertDialog.findViewById(R.id.tv_contact_mail)
            val number: TextView? = alertDialog.findViewById(R.id.tv_contact_number)




            mail?.apply {
                setOnClickListener {
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "message/rfc822"
                        putExtra(Intent.EXTRA_EMAIL, arrayOf("info@check-in.in"))
                    }
                    if (intent.resolveActivity(packageManager) != null) {
                        startActivity(intent)
                    }
                }
            }

            number?.apply {
                setOnClickListener {
                    val uri = "tel:+917406270659"
                    val intent = Intent(Intent.ACTION_CALL)
                    intent.data = Uri.parse(uri)
                    if (ContextCompat.checkSelfPermission(this@AboutUsActivity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this@AboutUsActivity, arrayOf(Manifest.permission.CALL_PHONE), 100)
                    } else {
                        startActivity(intent)
                    }
                }
            }


        }

        imFacebook.setOnClickListener {
            val facebookIntent = Intent(Intent.ACTION_VIEW)
            val facebookUrl = getFacebookPageURL()
            facebookIntent.data = Uri.parse(facebookUrl)
            startActivity(facebookIntent)
        }

        imGmail.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "message/rfc822"
                putExtra(Intent.EXTRA_EMAIL, arrayOf("info@check-in.in"))
            }
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            }
        }

        imYoutube.setOnClickListener {
            intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://www.youtube.com/channel/UCdQcZz5zv7zJZscHKL5-ZOw")
            startActivity(intent)
        }

        imInstagram.setOnClickListener {
            val uri = Uri.parse("http://instagram.com/_u/checkin.app")
            val intent = Intent(Intent.ACTION_VIEW, uri)

            intent.setPackage("com.instagram.android")

            try {
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                startActivity(Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://instagram.com/checkin.app")))
            }
        }

    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            100 -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    val uri = "tel:+917406270659"
                    val intent = Intent(Intent.ACTION_CALL)
                    intent.data = Uri.parse(uri)
                    startActivity(intent)
                }
            }
        }
    }

    private fun getFacebookPageURL(): String? {
        val facebookUrl = "https://www.facebook.com/checkinapp"
        val facebookPageId = "checkinapp"

        val packageManager: PackageManager = packageManager
        return try {
            val versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode
            if (versionCode >= 3002850) {
                "fb://facewebmodal/f?href=$facebookUrl"
            } else {
                "fb://page/$facebookPageId"
            }
        } catch (e: PackageManager.NameNotFoundException) {
            facebookUrl
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
