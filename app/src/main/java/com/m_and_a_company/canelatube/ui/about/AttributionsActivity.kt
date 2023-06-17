package com.m_and_a_company.canelatube.ui.about

import android.graphics.Color
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
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
import com.m_and_a_company.canelatube.R
import com.m_and_a_company.canelatube.databinding.ActivityAttributionsBinding

class AttributionsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAttributionsBinding
    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAttributionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.let {
            it.title = getString(R.string.lbl_our_other_attributes_title)
            it.setDisplayHomeAsUpEnabled(true)
            it.setDisplayShowHomeEnabled(true)
        }

        binding.tvAttributionsThanks.apply {
            movementMethod = LinkMovementMethod.getInstance()
            highlightColor = Color.TRANSPARENT

            append(fillAttributes("Facebook icons created by Freepik", "Flaticon", "https://www.flaticon.com/free-icons/facebook"))
            append(fillAttributes("Attribute icons created by Vectors Tank", "Flaticon", "https://www.flaticon.com/free-icons/attribute"))
            append(fillAttributes("Youtube icons created by Freepik", "Flaticon", "https://www.flaticon.com/free-icons/youtube"))
            append(fillAttributes("Patent icons created by itim2101", "Flaticon", "https://www.flaticon.com/free-icons/patent"))
            append(fillAttributes("Animations By Lottie Files", "Lottie", "https://lottiefiles.com/"))
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun fillAttributes(
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