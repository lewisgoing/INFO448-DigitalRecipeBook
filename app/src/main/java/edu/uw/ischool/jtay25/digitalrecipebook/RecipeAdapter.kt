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
    private val onRecipeClick: (Recipe) -> Unit // Callback for item clicks
) : RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {

    // ViewHolder represents a single recipe card in the RecyclerView
    inner class RecipeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.recipeTitle)
        val chef: TextView = view.findViewById(R.id.chefName)
        val duration: TextView = view.findViewById(R.id.duration)
        val image: ImageView = view.findViewById(R.id.recipeImage)

        init {
            view.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onRecipeClick(recipes[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recipe_card, parent, false)
        return RecipeViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = recipes[position]
        holder.title.text = recipe.title
        holder.chef.text = recipe.chef
        holder.duration.text = recipe.duration
    }

    override fun getItemCount(): Int = recipes.size
}
