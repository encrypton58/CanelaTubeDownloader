package com.m_and_a_company.canelatube.ui.about

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.MenuItem
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import com.m_and_a_company.canelatube.R
import com.m_and_a_company.canelatube.databinding.ActivityLicenciesBinding

class LicencesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLicenciesBinding
    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLicenciesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.let {
            it.title = getString(R.string.lbl_our_us_licences)
            it.setDisplayHomeAsUpEnabled(true)
            it.setDisplayShowHomeEnabled(true)
        }


        binding.tvLicences.apply {
            movementMethod = LinkMovementMethod.getInstance()
            highlightColor = Color.TRANSPARENT
            append(fillLicences("Retrofit", "by square", "https://square.github.io/retrofit/"))
            append(fillLicences("FFmpegMediaMetadataRetriever", "by William Seemann", "https://github.com/wseemann/FFmpegMediaMetadataRetriever"))
            append(fillLicences("Picasso", "by square", "https://square.github.io/picasso/"))
            append(fillLicences("Gson", "by Google", "https://github.com/google/gson"))
            append(fillLicences("Shimmer", "by Facebook", "https://facebook.github.io/shimmer-android/"))
            append(fillLicences("OkHttp", "by square", "https://square.github.io/okhttp/"))
            append(fillLicences("Lottie", "by Airbnb", "https://github.com/airbnb/lottie-android"))
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun fillLicences(
        linkColored: String,
        endLink: String,
        urlGo: String
    ): SpannableString {
        val fullText = "$linkColored - $endLink \n\n"
        val spannableString = SpannableString(fullText)

        val linkStartIndex = fullText.indexOf(linkColored)
        val linkEndIndex = linkStartIndex + linkColored.length

        val clickableSpan = object : ClickableSpan() {

            override fun onClick(widget: View) {
                callWebView(urlGo)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
            }

        }
        spannableString.setSpan(clickableSpan, linkStartIndex, linkEndIndex, 0)
        val colorSpan = ForegroundColorSpan(Color.RED)
        spannableString.setSpan(colorSpan, linkStartIndex, linkEndIndex, 0)
        val spannableStyle = StyleSpan(Typeface.BOLD)
        spannableString.setSpan(spannableStyle, linkStartIndex, linkEndIndex, 0)

        return spannableString
    }


    private fun callWebView(url: String) {
        webView = WebView(this)

        val webSettings: WebSettings = webView.settings
        webSettings.javaScriptEnabled = true

        webSettings.setSupportZoom(true)
        webSettings.builtInZoomControls = true
        webSettings.displayZoomControls = false

        webView.webChromeClient = WebChromeClient()

        webView.loadUrl(url)

        setContentView(webView)
    }

}