package com.checkin.app.checkin.AppDetails

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.checkin.app.checkin.R
import com.mikepenz.aboutlibraries.LibTaskCallback
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.LibsBuilder
import com.mikepenz.aboutlibraries.LibsConfiguration
import com.mikepenz.aboutlibraries.entity.Library
import com.mikepenz.fastadapter.adapters.ItemAdapter

class AboutLicenseFragmentActivity : AppCompatActivity() {

    internal var libTaskCallback: LibTaskCallback = object : LibTaskCallback {
        override fun onLibTaskStarted() {
            Log.e("AboutLibraries", "started")
        }

        override fun onLibTaskFinished(fastItemAdapter: ItemAdapter<*>) {
            Log.e("AboutLibraries", "finished")
        }
    }

    internal var libsUIListener: LibsConfiguration.LibsUIListener = object : LibsConfiguration.LibsUIListener {
        override fun preOnCreateView(view: View): View {
            return view
        }

        override fun postOnCreateView(view: View): View {
            return view
        }
    }

    internal var libsListener: LibsConfiguration.LibsListener = object : LibsConfiguration.LibsListener {
        override fun onIconClicked(v: View) {
            Toast.makeText(v.context, "We are able to track this now ;)", Toast.LENGTH_LONG).show()
        }

        override fun onLibraryAuthorClicked(v: View, library: Library): Boolean {
            return false
        }

        override fun onLibraryContentClicked(v: View, library: Library): Boolean {
            return false
        }

        override fun onLibraryBottomClicked(v: View, library: Library): Boolean {
            return false
        }

        override fun onExtraClicked(v: View, specialButton: Libs.SpecialButton): Boolean {
            return false
        }

        override fun onIconLongClicked(v: View): Boolean {
            return false
        }

        override fun onLibraryAuthorLongClicked(v: View, library: Library): Boolean {
            return false
        }

        override fun onLibraryContentLongClicked(v: View, library: Library): Boolean {
            return false
        }

        override fun onLibraryBottomLongClicked(v: View, library: Library): Boolean {
            return false
        }
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_license)

        val fragment = LibsBuilder()
                .withVersionShown(false)
                .withLicenseShown(true)
                .withAboutIconShown(true)
                .withAboutAppName("Checkin")
                .withLibraryModification("checkin", Libs.LibraryFields.LIBRARY_NAME, "Checkin")
                .withAboutSpecial1("T & C")
                .withAboutSpecial1Description(getString(R.string.app_terms_and_condition_link))
                .withAboutSpecial2("Privacy Policy")
                .withAboutSpecial2Description(getString(R.string.app_privacy_policy_link))
                .withAboutSpecial3("Refund Policy")
                .withAboutSpecial3Description(getString(R.string.app_refund_policy_link))
                .withAboutVersionShown(true)
                .withAboutDescription(getString(R.string.about_us))
                .supportFragment()


        val fragmentManager = supportFragmentManager
        fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit()

    }
}