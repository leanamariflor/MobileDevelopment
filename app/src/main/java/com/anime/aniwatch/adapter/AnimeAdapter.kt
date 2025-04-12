import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.anime.aniwatch.R
import com.anime.aniwatch.data.Anime
import com.bumptech.glide.Glide

class AnimeAdapter(private val animeList: List<Anime>) :
    RecyclerView.Adapter<AnimeAdapter.AnimeViewHolder>() {

    class AnimeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val posterImageView: ImageView = view.findViewById(R.id.posterImageView)
        val nameTextView: TextView = view.findViewById(R.id.nameTextView)
        val episodesTextView: TextView = view.findViewById(R.id.episodesTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnimeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_anime, parent, false)
        return AnimeViewHolder(view)
    }

    override fun onBindViewHolder(holder: AnimeViewHolder, position: Int) {
        val anime = animeList[position]
        holder.nameTextView.text = anime.name
        holder.episodesTextView.text = "Sub: ${anime.episodes.sub ?: 0}, Dub: ${anime.episodes.dub ?: 0}"
        Glide.with(holder.itemView.context)
            .load(anime.poster)
            .into(holder.posterImageView)
    }

    override fun getItemCount(): Int = animeList.size
}
