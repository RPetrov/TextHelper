package rpetrov.texthelper.wiki


import com.google.gson.annotations.SerializedName

data class Warnings(
    @SerializedName("main")
    val main: Main,
    @SerializedName("parse")
    val parse: ParseX
)