package rpetrov.texthelper.wiki

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WikiService {

    @GET("w/api.php")
    fun getPage(
        @Query("page") page: String,
        @Query("action") action: String = "parse",
        @Query("prop") prop: String = "text",
        @Query("disablepp") disablepp: String = "1",
        @Query("format") format: String = "json"
    ): Call<WikiModel>
}