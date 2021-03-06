package moviesfinder.api

import com.google.gson.annotations.SerializedName

class SearchResults {

    @SerializedName("Response")
    private var response: String? = null

    @SerializedName("totalResults")
    private var totalResults: String? = null

    @SerializedName("Search")
    private var search: List<SearchItem>? = null

    fun getResponse(): String? {
        return response
    }

    fun getSearch(): List<SearchItem>? {
        return search
    }

    override fun toString(): String {
        return "Response{" +
                "response = '" + response + '\''.toString() +
                ",totalResults = '" + totalResults + '\''.toString() +
                ",search = '" + search + '\''.toString() +
                "}"
    }

    class SearchItem {

        @SerializedName("Type")
        var type: String? = null

        @SerializedName("Year")
        var year: String? = null

        @SerializedName("imdbID")
        var imdbID: String? = null

        @SerializedName("Poster")
        var poster: String? = null

        @SerializedName("Title")
        var title: String? = null

        override fun toString(): String {
            return "SearchItem{" +
                    "type = '" + type + '\''.toString() +
                    ",year = '" + year + '\''.toString() +
                    ",imdbID = '" + imdbID + '\''.toString() +
                    ",poster = '" + poster + '\''.toString() +
                    ",title = '" + title + '\''.toString() +
                    "}"
        }
    }
}