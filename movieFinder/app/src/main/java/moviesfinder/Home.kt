package moviesfinder

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.View
import android.widget.SearchView
import android.widget.Toast

import kotlinx.android.synthetic.main.activity_home.*
import moviesfinder.adapter.CustomAdapterMovie
import moviesfinder.api.ApiClient
import moviesfinder.api.ApiInterface
import moviesfinder.listener.PaginationScrollListener
import moviesfinder.listener.RecyclerItemClickListener
import moviesfinder.moviedetail.MovieDetailScrollingActivity
import moviesfinder.api.SearchResults
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList

class Home : AppCompatActivity(){


    var PAGE_START = 1
    var isLoading = false
    var isLastPage = false
    var TOTAL_PAGES = 20
    var currentPage = PAGE_START
    lateinit var s1: String
    lateinit var linearLayoutManager: LinearLayoutManager
    lateinit var apiInterface: ApiInterface
    internal lateinit var searchView: SearchView
    internal var searchResultsList: MutableList<SearchResults.SearchItem> = ArrayList()
    lateinit var mAdapter: CustomAdapterMovie

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        apiInterface = ApiClient.getClient().create(ApiInterface::class.java)

        mAdapter = CustomAdapterMovie(searchResultsList)
        linearLayoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
        movie_recycler_view.setLayoutManager(linearLayoutManager)
        movie_recycler_view.setItemAnimator(DefaultItemAnimator())
        movie_recycler_view.setAdapter(mAdapter)
        movie_recycler_view.addOnItemTouchListener(
            RecyclerItemClickListener(applicationContext, object : RecyclerItemClickListener.OnItemClickListener {
                override fun onItemClick(view: View, position: Int) {
                    val searchItem = searchResultsList[position]
                    val intent = Intent(applicationContext, MovieDetailScrollingActivity::class.java)
                    intent.putExtra("poster", searchItem.poster)
                    intent.putExtra("title", searchItem.title)
                    startActivity(intent)
                }

            })
        )
        movie_recycler_view.addOnScrollListener(object : PaginationScrollListener(linearLayoutManager) {
            override fun getTotalPageCount(): Int {
                return TOTAL_PAGES
            }

            override fun isLastPage(): Boolean {
                return isLastPage
            }

            override fun isLoading(): Boolean {
                return isLoading
            }

            override fun loadMoreItems() {
                isLoading = true
                currentPage += 1

                // mocking network delay for API call
                Handler().postDelayed({ loadNextPage(s1) }, 1000)
            }
        })
        linear_layout.visibility = View.VISIBLE
        movie_recycler_view.visibility = View.GONE
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.search, menu)
        searchView = menu.findItem(R.id.search).actionView as SearchView
        searchView.setQueryHint("Search")
        searchView.setSubmitButtonEnabled(true)
        searchView.onActionViewExpanded()
        search(searchView)
        return true
    }

    private fun search(searchView: SearchView) {

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                var query = query
                query = query.toLowerCase()

                    isLoading = false
                    isLastPage = false
                    currentPage = PAGE_START
                    searchResultsList.clear()
                    getSearchResultMoviesData(query)
                    searchView.onActionViewCollapsed()

                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {

                return false
            }
        })
    }

    fun getSearchResultMoviesData(s: String) {
        s1 = s
        movie_recycler_view.setVisibility(View.VISIBLE)
        linear_layout.setVisibility(View.GONE)
        movie_recycler_view.showShimmerAdapter()
        val call = apiInterface.getSearchResultData(s, "69841868", currentPage)
        call.enqueue(object : Callback<SearchResults> {
            override fun onResponse(call: Call<SearchResults>, response: retrofit2.Response<SearchResults>) {
                movie_recycler_view.hideShimmerAdapter()
                if (response.isSuccessful) {
                    if (response.body()!!.getResponse().equals("True")) {
                        searchResultsList.addAll(response.body()!!.getSearch()!!)
                        if (currentPage <= TOTAL_PAGES)
                            mAdapter.addLoadingFooter()
                        else
                            isLastPage = true
                    } else {
                        movie_recycler_view.setVisibility(View.GONE)
                        linear_layout.setVisibility(View.VISIBLE)
                        homeImg.setImageResource(R.drawable.soitem)
                        homeTxt.setText("Too many results!")
                        Toast.makeText(applicationContext, "Too many results!", Toast.LENGTH_SHORT).show()
                    }
                } else
                    Toast.makeText(applicationContext, "Error", Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(call: Call<SearchResults>, t: Throwable) {
                movie_recycler_view.hideShimmerAdapter()
                Toast.makeText(applicationContext, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }


    fun loadNextPage(s: String) {

        val call = apiInterface.getSearchResultData(s, "69841868", currentPage)
        call.enqueue(object : Callback<SearchResults> {
            override fun onResponse(call: Call<SearchResults>, response: Response<SearchResults>) {
                if (response.isSuccessful) {
                    if (response.body()!!.getResponse().equals("True")) {
                        mAdapter.removeLoadingFooter()
                        isLoading = false
                        searchResultsList.addAll(response.body()!!.getSearch()!!)
                        if (currentPage != TOTAL_PAGES)
                            mAdapter.addLoadingFooter()
                        else
                            isLastPage = true
                    } else {
                        isLoading = false
                        isLastPage = true
                        mAdapter.removeLoadingFooter()
                        Toast.makeText(applicationContext, "I think i couldn't find it", Toast.LENGTH_SHORT).show()
                    }
                } else
                    Toast.makeText(applicationContext, "Error", Toast.LENGTH_SHORT).show()

            }

            override fun onFailure(call: Call<SearchResults>, t: Throwable) {
                Toast.makeText(applicationContext, t.message, Toast.LENGTH_SHORT).show()
            }
        })


    }


}
