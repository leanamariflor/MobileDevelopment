import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.anime.aniwatch.AnimeAboutActivity
import com.anime.aniwatch.R
import com.anime.aniwatch.data.Anime
import com.bumptech.glide.Glide

class AnimeAdapter(private val animeList: List<Anime>) :
    RecyclerView.Adapter<AnimeAdapter.AnimeViewHolder>() {

    private var lastClickTime = 0L

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
        val anime = animeList.getOrNull(position) ?: return
        holder.nameTextView.text = anime.name
        holder.episodesTextView.text = "Sub: ${anime.episodes.sub ?: 0}, Dub: ${anime.episodes.dub ?: 0}"
        Glide.with(holder.itemView.context)
            .load(anime.poster)
            .into(holder.posterImageView)

        holder.itemView.setOnClickListener {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastClickTime > 500) {
                lastClickTime = currentTime
                val context = holder.itemView.context
                val intent = Intent(context, AnimeAboutActivity::class.java)
                intent.putExtra("ANIME_ID", anime.id)
                context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int = animeList.size
}