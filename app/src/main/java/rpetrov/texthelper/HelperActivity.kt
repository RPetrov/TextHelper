package rpetrov.texthelper

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Html
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.toSpannable
import androidx.core.view.isVisible
import com.google.android.material.bottomsheet.BottomSheetBehavior
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import rpetrov.texthelper.wiki.WikiModel
import rpetrov.texthelper.wiki.WikiService


/**
 * 1. share text
 * 2. Regex("[^\\p{L}0-9']+")
 * 2.1 spannable
 * 3. ClickableSpan
 * 4. https://ru.wiktionary.org/w/api.php?action=parse&page=%D0%B3%D0%BE%D1%80%D0%BE%D0%B4&prop=text&disablepp=1&format=json
 * 5. retrofit, json, result
 */
class HelperActivity : AppCompatActivity() {

    val progressBar by lazy {
        findViewById<ProgressBar>(R.id.progress_bar)
    }


    val wikiText by lazy {
        findViewById<TextView>(R.id.wikiText)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.helper_activity)

        val wikiService = Retrofit
            .Builder()
            .baseUrl("https://ru.wiktionary.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(WikiService::class.java)


        val bs = BottomSheetBehavior.from(findViewById<TextView>(R.id.frame_layout))
        bs.state = BottomSheetBehavior.STATE_HIDDEN

        if (intent.action == Intent.ACTION_SEND) {
            val text = intent.extras?.getString(Intent.EXTRA_TEXT) ?: return

            var lastIndex = 0
            val spannableStringBuilder = SpannableStringBuilder(text)

            val words = text.split(Regex("[^\\p{L}0-9']+"))
            words.forEachIndexed { index, word ->
                val indexOf = text.indexOf(word, lastIndex)
                lastIndex = indexOf + word.length
                spannableStringBuilder.setSpan(
                    object : ClickableSpan() {
                        override fun onClick(widget: View) {
                            wikiService.getPage(word).enqueue(object : Callback<WikiModel?> {
                                override fun onResponse(call: Call<WikiModel?>, response: Response<WikiModel?>) {
                                    progressBar.isVisible = false
                                    Log.e("HA", response.body()?.parse?.text?.text ?: "NO TEXT")
                                    wikiText.text = Html.fromHtml(response.body()?.parse?.text?.text , Html.FROM_HTML_MODE_LEGACY)
                                }

                                override fun onFailure(call: Call<WikiModel?>, t: Throwable) {
                                    Log.e("HA", t.message, t)
                                }
                            })

                            progressBar.isVisible = true
                            wikiText.text = null
                            val bs = BottomSheetBehavior.from(findViewById<TextView>(R.id.frame_layout))
                            bs.state = BottomSheetBehavior.STATE_HALF_EXPANDED
                        }

                        override fun updateDrawState(ds: TextPaint) {
                            super.updateDrawState(ds)
                            ds.isUnderlineText = false
                            ds.color = Color.BLACK
                        }
                    },
                    indexOf,
                    indexOf + word.length,
                    SpannableString.SPAN_INCLUSIVE_EXCLUSIVE
                )
            }


            findViewById<TextView>(R.id.text).also {
                it.text = spannableStringBuilder.toSpannable()
                it.movementMethod = LinkMovementMethod()

            }
        }

    }
}