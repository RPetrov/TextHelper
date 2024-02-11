package rpetrov.texthelper.wiki


import com.google.gson.annotations.SerializedName

data class Parse(
    @SerializedName("pageid")
    val pageid: Int,
    @SerializedName("text")
    val text: Text,
    @SerializedName("title")
    val title: String
)