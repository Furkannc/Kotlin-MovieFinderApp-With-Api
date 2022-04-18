package moviesfinder.moviedetail

import android.os.Bundle

import android.support.v7.app.AppCompatActivity

import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

import kotlinx.android.synthetic.main.activity_movie_detail_scrolling.*
import kotlinx.android.synthetic.main.content_movie_detail_scrolling.*
import moviesfinder.R
import moviesfinder.api.ApiClient
import moviesfinder.api.ApiInterface
import moviesfinder.api.MovieDetail
import retrofit2.Call
import retrofit2.Callback

class MovieDetailScrollingActivity : AppCompatActivity() {

    lateinit var apiInterface: ApiInterface

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_detail_scrolling)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        apiInterface = ApiClient.getClient().create(ApiInterface::class.java)

        val intent =getIntent();
        toolbar_layout.title =intent.getStringExtra("title")
        Glide.with(applicationContext).load(intent.getStringExtra("poster"))
            .asBitmap().centerCrop()
            .thumbnail(0.5f)
            .placeholder(R.drawable.ic_launcher_background)
            .centerCrop()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(poster)
        card_view.setVisibility(View.GONE)

            getMovieData(intent.getStringExtra("title"))
    }

    fun getMovieData(s: String) {
        progressbar.visibility = View.VISIBLE
        val call = apiInterface.getMovieDetailData(s, "69841868")
        call.enqueue(object : Callback<MovieDetail> {
            override fun onResponse(call: Call<MovieDetail>, response: retrofit2.Response<MovieDetail>) {
                progressbar.visibility = View.GONE
                if (response.isSuccessful) {
                    card_view.setVisibility(View.VISIBLE)
                    year.text = "Year: " + response.body()!!.getYear()
                    director.text = "Director: " + response.body()!!.getDirector()
                    writer.text = "Writer: " + response.body()!!.getWriter()
                    plot.text = response.body()!!.getPlot()
                    if(response.body()!!.getRatings()!!.size!=0)
                        imd.text = "Internet Movie Database: " + response.body()!!.getRatings()!!.get(0).value
                    metascore.text = "Metascore: " + response.body()!!.getMetascore()
                    imdb_rating.setText("IMBD Rating: " + response.body()!!.getImdbrating())

                } else
                    Toast.makeText(applicationContext, "Error", Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(call: Call<MovieDetail>, t: Throwable) {
                progressbar.visibility = View.GONE
                Toast.makeText(applicationContext, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
