package com.example.burningbushent.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.burningbushent.Models.Movie;
import com.example.burningbushent.R;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;
import java.util.List;


public class CustomMoviesAdapter extends RecyclerView.Adapter<CustomMoviesAdapter.CustomMovieViewHolder> {

    private static final String TAG = "CustomMoviesAdapter";
    private Context context;
    private List<Movie> listMovies;

    final private MovieItemClickListener mOnMovieItemClickListener;

    public interface MovieItemClickListener {
        void onMovieItemClick(int clickedItemIndex);
    }

    public CustomMoviesAdapter(Context context, List<Movie> listMovies, MovieItemClickListener mOnMovieItemClickListener) {
        this.context = context;
        this.listMovies = listMovies;
        this.mOnMovieItemClickListener = mOnMovieItemClickListener;
    }

    @Override
    public CustomMoviesAdapter.CustomMovieViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_grid_movie_item, parent, false);
        return new CustomMovieViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder( CustomMoviesAdapter.CustomMovieViewHolder holder, int position) {
        holder.bindMovie(listMovies.get(position));
    }

    @Override
    public int getItemCount() {
        Log.v(TAG,"All Movies from listMovies" + listMovies.size());
        return listMovies.size();
    }


    public class CustomMovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public Context mContext;
        public TextView movieTitle;
        public ImageView coverImage;
        public TextView releaseDate;

        public CustomMovieViewHolder( View itemView) {
            super(itemView);
            coverImage = itemView.findViewById(R.id.ivMovieImage);
            movieTitle = itemView.findViewById(R.id.tvMovieTitle);
            releaseDate = itemView.findViewById(R.id.tvReleaseDate);

            mContext = itemView.getContext();
            itemView.setOnClickListener(this);
        }
        void bindMovie(Movie movie) {
            StringBuilder releaseText = new StringBuilder().append("Release Date: ");
            releaseText.append(movie.getReleaseDate());
            Log.d(TAG,"The string Builder is: " + releaseText);

            movieTitle.setText(movie.getOriginalTitle());
            releaseDate.setText(releaseText);

            Picasso.Builder builder = new Picasso.Builder(context);
            builder.downloader(new OkHttp3Downloader(context));
            builder.build().load(context.getResources().getString(R.string.IMAGE_BASE_URL) + movie.getPosterPath())
                    .placeholder((R.drawable.gradient_background))
                    .error(R.drawable.ic_launcher_background)
                    .into(coverImage);
        }
        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mOnMovieItemClickListener.onMovieItemClick(clickedPosition);
        }
    }
}
