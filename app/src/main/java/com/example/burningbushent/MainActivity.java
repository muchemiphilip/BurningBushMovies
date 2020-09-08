package com.example.burningbushent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.burningbushent.Adapters.CustomMoviesAdapter;
import com.example.burningbushent.Interface.MovieInterface;
import com.example.burningbushent.Models.Movie;
import com.example.burningbushent.Models.MovieResponse;
import com.example.burningbushent.Network.APIClient;
import com.example.burningbushent.Utils.MovieUtils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    public RecyclerView rvMain;
    public ProgressBar progressBar;
    private static Retrofit retrofit;
    private static String API_KEY;
    public List<Movie> movies;
    private int currentPage = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(R.string.popular_movies);
        API_KEY = getResources().getString(R.string.API_KEY);

        rvMain = findViewById(R.id.rv_main);
        progressBar = findViewById(R.id.progressBar);

        progressBar.setVisibility(View.VISIBLE);
        getPopularMovies();
    }

    private void getPopularMovies() {
        if (MovieUtils.getInstance().isNetworkAvailable(this)){
            if (retrofit == null){
                retrofit = APIClient.getRetrofitInstance();
            }
            MovieInterface movieInterface = retrofit.create(MovieInterface.class);
            Call<MovieResponse> call = movieInterface.getPopularMovies(API_KEY, getResources().getString(R.string.LANGUAGE), currentPage);
            Log.i("Popular movies api", movieInterface.getPopularMovies(API_KEY, getResources().getString(R.string.LANGUAGE), 1).request().url().toString());

            Log.d(TAG, "On Response from call enqueue");
            call.enqueue(new Callback<MovieResponse>() {
                @Override
                public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                    Log.d(TAG, "Checking if response is successful");
                    if (response.isSuccessful()){
                        progressBar.setVisibility(View.INVISIBLE);
                        movies = response.body().getResults();
                        if (movies != null){
                            generateMovieList(movies);
                            Log.d(TAG, "Number of popular movies received: " + movies.size());
                        }
                    }
                }
                @Override
                public void onFailure(Call<MovieResponse> call, Throwable t) {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(MainActivity.this, "Something went wrong... Please try later.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Log.i("Network Connection Status", "Not Available");
        }
    }
    private void generateMovieList(List<Movie> results) {
        CustomMoviesAdapter adapter = new CustomMoviesAdapter(this, results, new CustomMoviesAdapter.MovieItemClickListener() {
            @Override
            public void onMovieItemClick(int clickedItemIndex) {
                Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                intent.putExtra("Movie", results.get(clickedItemIndex));
                startActivity(intent);
            }
        });
        initializeGridView(adapter);
    }

    private void initializeGridView(CustomMoviesAdapter adapter) {
        rvMain.setHasFixedSize(true);
        rvMain.setLayoutManager(new GridLayoutManager(this, 2));
        rvMain.setAdapter(adapter);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.topRated:
                //getTopRatedMovies();
                setTitle(R.string.toprated_movies);
                break;
            case R.id.popular:
                getPopularMovies();
                setTitle(R.string.popular_movies);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}