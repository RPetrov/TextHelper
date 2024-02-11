package rpetrov.texthelper.wiki

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


interface WikiService {

    // https://ru.wiktionary.org/w/api.php?action=parse&page=%D0%B3%D0%BE%D1%80%D0%BE%D0%B4&prop=text&disablepp=1&format=json

    @GET("w/api.php")
    fun search(
        @Query("page") page: String,
        @Query("action") action: String = "parse",
        @Query("prop") prop: String = "text",
        @Query("disablepp") utf8: String = "1",
        @Query("format") format: String = "json"
    ): Call<WikiResponse?>


}