package rpetrov.texthelper.wiki


import com.google.gson.annotations.SerializedName

data class WikiModel(
    @SerializedName("parse")
    val parse: Parse,
    @SerializedName("warnings")
    val warnings: Warnings
)