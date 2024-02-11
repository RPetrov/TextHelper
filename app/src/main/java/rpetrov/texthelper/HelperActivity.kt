package rpetrov.texthelper

import android.animation.ValueAnimator
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.BackgroundColorSpan
import android.text.style.ClickableSpan
import android.view.View
import android.view.textclassifier.TextLinks.TextLinkSpan
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.TextView.BufferType
import android.widget.Toast
import android.widget.ViewAnimator
import androidx.core.text.HtmlCompat
import androidx.core.view.isVisible
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import rpetrov.texthelper.wiki.WikiResponse
import rpetrov.texthelper.wiki.WikiService
import java.io.File
import java.net.URI
import java.nio.file.Files
import java.nio.file.Paths
import java.util.StringTokenizer

/**
 * 1. share text
 * 2.Regex("[^\\p{L}0-9']+")
 * 3. ClickableSpan
 * https://ru.wiktionary.org/w/api.php?action=parse&page=%D0%B3%D0%BE%D1%80%D0%BE%D0%B4&prop=text&disablepp=1&format=json
 */
class HelperActivity : AppCompatActivity() {

    private val history = mutableListOf<String>()

    val retrofit = Retrofit.Builder()
        .baseUrl("https://ru.wikipedia.org/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val wikiService = retrofit.create(WikiService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_helper)

        when (intent?.action) {
            Intent.ACTION_SEND -> {
                if ("text/plain" == intent.type) {
                    intent.getStringExtra(Intent.EXTRA_TEXT)?.let {
                        findViewById<TextView>(R.id.text).isVisible = true
                        findViewById<TextView>(R.id.text).setText(processText(it), BufferType.SPANNABLE)
                        findViewById<TextView>(R.id.text).setMovementMethod(LinkMovementMethod.getInstance());
                    }
                }
            }

            else -> {
                findViewById<TextView>(R.id.text).isVisible = true
                findViewById<TextView>(R.id.text).setText(processText("Kotlin (Ко́тлин) — статически типизированный, объектно-ориентированный язык программирования, работающий поверх Java Virtual Machine и разрабатываемый компанией JetBrains. Также компилируется в JavaScript и в исполняемый код ряда платформ через инфраструктуру LLVM. Язык назван в честь российского острова Котлин в Финском заливе, на котором расположен город Кронштадт[4]."))
                findViewById<TextView>(R.id.text).setMovementMethod(LinkMovementMethod.getInstance())
            }
        }
    }

    private fun processText(text: String): SpannableString {

        val spannable = SpannableString(text)
        text.split(Regex("[^\\p{L}0-9']+")).forEach { word ->
            text.allIndexOf(word).forEach {
                spannable.setSpan(object : ClickableSpan(){
                    override fun onClick(p0: View) {
                        history.add(word)
                        spannable.setSpan(BackgroundColorSpan(Color.parseColor("#aa0000")), it, it + word.length, 0)
                        findViewById<TextView>(R.id.text).setText(spannable)
                        showPanel(word)
                      //  Toast.makeText(this@HelperActivity, word, Toast.LENGTH_LONG).show()
                    }

                    override fun updateDrawState(ds: TextPaint) {
                        super.updateDrawState(ds)
                        ds.isUnderlineText = false
                        ds.color = Color.BLACK
                    }
                }, it, it + word.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

                if(history.contains(word)){
                    spannable.setSpan(BackgroundColorSpan(Color.parseColor("#aa0000")), it, it + word.length, 0)
                }

            }
        }

        return spannable
    }

    private fun String.allIndexOf(word: String): List<Int> {
        return Regex("(?=$word)").findAll(this).map { it.range.first }.toList()
    }

    private fun showPanel(word: String){
        if(findViewById<FrameLayout>(R.id.panel).translationY != 0F ) return

        with(ValueAnimator.ofFloat(0F, -findViewById<FrameLayout>(R.id.panel).height.toFloat())){
            addUpdateListener {
                findViewById<FrameLayout>(R.id.panel).translationY = it.animatedValue as Float
            }
            duration = 300
            start()
        }

        wikiService.search(word).enqueue(object : Callback<WikiResponse?> {
            override fun onResponse(call: Call<WikiResponse?>, response: Response<WikiResponse?>) {
                findViewById<TextView>(R.id.wiki).text = Html.fromHtml(response.body()?.parse?.text?.x, HtmlCompat.FROM_HTML_MODE_LEGACY)
            }

            override fun onFailure(call: Call<WikiResponse?>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun hidePanel(){

    }

}