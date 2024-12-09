package edu.uw.ischool.jtay25.digitalrecipebook
import android.util.Log
import edu.uw.ischool.jtay25.digitalrecipebook.Recipe
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RecipeAdapter(
    private val recipes: List<Recipe>,
    private val onRecipeClick: (Recipe) -> Unit // Callback for card clicks
) : RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {

    inner class RecipeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val titleText: TextView = view.findViewById(R.id.recipeTitle)
        private val chefText: TextView = view.findViewById(R.id.chefName)
        private val durationText: TextView = view.findViewById(R.id.duration)
        private val bookmark: ImageView = view.findViewById(R.id.bookmarkIcon)


        fun bind(recipe: Recipe) {
            val tagRef = com.google.firebase.database.FirebaseDatabase.getInstance().getReference("recipes").child(recipe.id).child("tag")
            var currentTag : Boolean
            titleText.text = recipe.title
            chefText.text = "By ${recipe.chef}"
            durationText.text = recipe.duration
            tagRef.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    currentTag = task.result?.value as? Boolean == true
                    when (currentTag) {
                        true -> bookmark.setImageResource(R.drawable.icon_bookmark_added)
                        false -> bookmark.setImageResource(R.drawable.icon_bookmark)
                    }
                    bookmark.setOnClickListener {
                        if (!currentTag) {
                            // If tag is not true, set it to true (added to bookmarks)
                            tagRef.setValue(true)
                            bookmark.setImageResource(R.drawable.icon_bookmark_added)
                        } else {
                            // If tag is already true, set it to false (removed from bookmarks)
                            tagRef.setValue(false)
                            bookmark.setImageResource(R.drawable.icon_bookmark)
                        }
                    }
                }

            }

            // Set click listener for navigating to details
            itemView.setOnClickListener {
                onRecipeClick(recipe) // Trigger callback
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recipe_card, parent, false)
        return RecipeViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        holder.bind(recipes[position])
    }

    override fun getItemCount(): Int = recipes.size
}

