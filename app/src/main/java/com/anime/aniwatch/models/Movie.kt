import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.anime.aniwatch.R
import com.anime.aniwatch.data.Anime
import com.bumptech.glide.Glide

class MovieAdapter(
    private val context: Context,
    private var movies: List<Anime>,
    private val onClickItem: (Anime) -> Unit
) : BaseAdapter() {

    override fun getCount(): Int = movies.size
    override fun getItem(position: Int): Anime = movies[position]
    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.movie_item, parent, false)
        val movie = movies[position]

        val imageView: ImageView = view.findViewById(R.id.moviePoster)
        val titleView: TextView = view.findViewById(R.id.movieTitle)

        Glide.with(context).load(movie.poster).into(imageView)
        titleView.text = movie.name

        view.setOnClickListener { onClickItem(movie) }
        return view
    }

    fun updateList(newMovies: List<Anime>) {
        movies = newMovies
        notifyDataSetChanged()
    }
}