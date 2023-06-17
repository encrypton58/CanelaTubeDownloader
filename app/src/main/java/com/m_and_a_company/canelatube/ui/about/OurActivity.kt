package com.m_and_a_company.canelatube.ui.about

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.m_and_a_company.canelatube.BuildConfig
import com.m_and_a_company.canelatube.R
import com.m_and_a_company.canelatube.databinding.ActivityOurBinding
import com.m_and_a_company.canelatube.databinding.BottomSheetRegisterChangesBinding
import com.m_and_a_company.canelatube.domain.data.models.ChangeVersionModel
import com.squareup.picasso.Picasso

class OurActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOurBinding

    companion object {
        private const val URL_FACEBOOK_PAGE =
            "https://www.facebook.com/profile.php?id=100066555792472"
        private const val URL_WEB_PAGE = "http://canelatube.alwaysdata.net/"
        private const val URL_YOUTUBE_CHANNEL =
            "https://www.youtube.com/channel/UCqOPhrP8ONa3PHisG5zMH_w"
        private const val PACKAGE_YOUTUBE = "com.google.android.youtube"
        private const val AVATAR_GITHUB_IMAGE = "https://avatars.githubusercontent.com/u/57544377?v=4"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOurBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
        supportActionBar?.let {
            it.title = getString(R.string.lbl_menu_item_our)
            it.setDisplayHomeAsUpEnabled(true)
            it.setDisplayShowHomeEnabled(true)
        }
    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initViews() {
        binding.apply {
            Picasso.get().load(AVATAR_GITHUB_IMAGE).into(ivOurPrincipalDeveloper)
            ourSocialWeb.apply {
                genericTitle.text = getString(R.string.lbl_our_social_web_title)
                genericSubtitle.text = getString(R.string.lbl_our_social_web_sub)
                genericIcon.setImageResource(R.drawable.icon_web)
                genericRowLl.setOnClickListener { clickOption(TypeOptions.SOCIAL_WEB) }
            }
            ourSocialFacebook.apply {
                genericTitle.text = getString(R.string.lbl_our_social_facebook_title)
                genericSubtitle.text = getString(R.string.lbl_our_social_facebook_sub)
                genericIcon.setImageResource(R.drawable.facebook)
                genericRowLl.setOnClickListener { clickOption(TypeOptions.SOCIAL_FACEBOOK) }
            }
            ourSocialYoutube.apply {
                genericTitle.text = getString(R.string.lbl_our_social_youtube_title)
                genericSubtitle.text = getString(R.string.lbl_our_social_youtube_sub)
                genericIcon.setImageResource(R.drawable.youtube)
                genericRowLl.setOnClickListener { clickOption(TypeOptions.SOCIAL_YOUTUBE) }
            }
            ourOtherChanges.apply {
                genericTitle.text = getString(R.string.lbl_our_other_changes_title)
                genericSubtitle.text = getString(R.string.lbl_our_other_changes_sub)
                genericIcon.setImageResource(R.drawable.icon_changes)
                genericRowLl.setOnClickListener { clickOption(TypeOptions.OTHER_CHANGES) }

            }
            ourOtherLicense.apply {
                genericTitle.text = getString(R.string.lbl_our_other_license_title)
                genericSubtitle.text = getString(R.string.lbl_our_other_license_sub)
                genericIcon.setImageResource(R.drawable.certificate)
                genericRowLl.setOnClickListener { clickOption(TypeOptions.OTHER_LICENSE) }

            }
            ourOtherAttributes.apply {
                genericTitle.text = getString(R.string.lbl_our_other_attributes_title)
                genericSubtitle.text = getString(R.string.lbl_our_other_attributes_sub)
                genericIcon.setImageResource(R.drawable.quality)
                genericRowLl.setOnClickListener { clickOption(TypeOptions.OTHER_ATTRIBUTES) }
            }
            ourOtherVersion.apply {
                genericTitle.text = getString(R.string.lbl_our_other_version_title)
                genericSubtitle.text = BuildConfig.VERSION_NAME
                genericIcon.setImageResource(R.drawable.icon_error)
            }
        }
    }

    private fun clickOption(typeSelect: TypeOptions) {

        when (typeSelect) {
            TypeOptions.SOCIAL_FACEBOOK -> openFacebookPage()
            TypeOptions.SOCIAL_WEB -> openUrlInBrowser(URL_WEB_PAGE)
            TypeOptions.SOCIAL_YOUTUBE -> openYoutubeChannel()

            TypeOptions.OTHER_CHANGES -> openRegisterChanges()
            TypeOptions.OTHER_LICENSE -> openLicences()
            TypeOptions.OTHER_ATTRIBUTES -> openAttributions()
        }

    }


    private fun openUrlInBrowser(url: String) {
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse(url)
            )
        )
    }

    private fun openFacebookPage() {
        try {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("fb://profile/100066555792472")
                )
            )
        } catch (e: Exception) {
            openUrlInBrowser(URL_FACEBOOK_PAGE)
        }
    }

    private fun openYoutubeChannel() {
        try {
            startActivity(Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(URL_YOUTUBE_CHANNEL)
                setPackage(PACKAGE_YOUTUBE)
            })
        } catch (e: ActivityNotFoundException) {
            openUrlInBrowser(URL_YOUTUBE_CHANNEL)
        }
    }

    private fun openRegisterChanges() {
        val bottomSheet = BottomSheetDialog(this, R.style.AppBottomSheetDialogTheme)
        val bottomSheetBinding = BottomSheetRegisterChangesBinding.inflate(layoutInflater)
        bottomSheet.setContentView(bottomSheetBinding.root)
        bottomSheetBinding.rvBottomRegisterChange.apply {
            layoutManager = LinearLayoutManager(applicationContext)
            adapter = getChanges()
        }
        bottomSheet.show()

    }

    private fun openLicences() {
        startActivity(Intent(applicationContext, LicencesActivity::class.java))
    }

    private fun openAttributions() {
        startActivity(Intent(applicationContext, AttributionsActivity::class.java))
    }

    private fun getChanges(): VersionChangesAdapter {
        return VersionChangesAdapter(
            arrayListOf(
                ChangeVersionModel(
                    "Se modifican estilos y se agrega splash screen",
                    "Marzo 27, 2023",
                    "Se mejoro las vistas en modo oscura ya que no concordaban con el diseño",
                    "1.0.1"
                ),
                ChangeVersionModel(
                    "Se cambio la orientación permitida en la app asi como una mejora en los estilos",
                    "Marzo 28, 2023",
                    "Se arreglo la vista en modo oscuro y también el solo permitir modo retrato",
                    "1.0.2"
                ),
                ChangeVersionModel(
                    "Se agrega pantalla Acerca de, Se agrego mensaje de cuando una canción dura mas de 5 minutos debe esperar un poco",
                    "Mayo 31, 2023",
                    "Se repara error de cuando se da back en la pantalla de selección del tipo de descarga se quedaba abierto la actividad y no se cerraba",
                    "1.0.3"
                )
            )
        )
    }

}

private enum class TypeOptions {
    SOCIAL_FACEBOOK,
    SOCIAL_WEB,
    SOCIAL_YOUTUBE,
    OTHER_CHANGES,
    OTHER_LICENSE,
    OTHER_ATTRIBUTES
}
