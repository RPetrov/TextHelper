package rpetrov.texthelper.wiki


import com.google.gson.annotations.SerializedName

data class Text(
    @SerializedName("*")
    val text: String
)