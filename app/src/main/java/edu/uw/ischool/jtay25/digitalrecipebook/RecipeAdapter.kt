package edu.uw.ischool.jtay25.digitalrecipebook
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

        fun bind(recipe: Recipe) {
            titleText.text = recipe.title
            chefText.text = "By ${recipe.chef}"
            durationText.text = recipe.duration

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

