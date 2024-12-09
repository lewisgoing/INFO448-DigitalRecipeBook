package edu.uw.ischool.jtay25.digitalrecipebook

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class RecipeAdapter(
    private val recipes: List<Recipe>,
    private val onRecipeClick: (Recipe) -> Unit
) : RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {

    inner class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleText: TextView = itemView.findViewById(R.id.recipeTitle)
        private val chefText: TextView = itemView.findViewById(R.id.chefName)
        private val durationText: TextView = itemView.findViewById(R.id.duration)
        private val bookmark: ImageView = itemView.findViewById(R.id.bookmarkIcon)

        fun bind(recipe: Recipe) {
            titleText.text = recipe.title
            chefText.text = "By ${recipe.chef}"
            durationText.text = recipe.duration

            updateBookmarkIcon(recipe.id)

            bookmark.setOnClickListener {
                toggleFavoriteStatus(recipe)
            }

            itemView.setOnClickListener {
                onRecipeClick(recipe)
            }
        }

        private fun toggleFavoriteStatus(recipe: Recipe) {
            val sharedPreferences = itemView.context.getSharedPreferences("RecipePrefs", Context.MODE_PRIVATE)
            val gson = Gson()
            val recipesJson = sharedPreferences.getString("recipes", "[]")
            val type = object : TypeToken<ArrayList<FavoriteRecipesActivity.RecipeData>>() {}.type
            val recipes: ArrayList<FavoriteRecipesActivity.RecipeData> = gson.fromJson(recipesJson, type)

            val isFavorite = recipes.any { it.id == recipe.id }
            if (isFavorite) {
                recipes.removeAll { it.id == recipe.id }
                bookmark.setImageResource(R.drawable.icon_bookmark)
            } else {
                recipes.add(FavoriteRecipesActivity.RecipeData(
                    recipe.id,
                    recipe.title,
                    recipe.chef,
                    recipe.duration,
                    // 0
                ))
                bookmark.setImageResource(R.drawable.icon_bookmark_added)
            }

            val editor = sharedPreferences.edit()
            editor.putString("recipes", gson.toJson(recipes))
            editor.apply()

            // Update Firebase
            val tagRef = FirebaseDatabase.getInstance().getReference("recipes").child(recipe.id).child("tag")
            tagRef.setValue(!isFavorite)
        }

        private fun updateBookmarkIcon(recipeId: String) {
            val sharedPreferences = itemView.context.getSharedPreferences("RecipePrefs", Context.MODE_PRIVATE)
            val gson = Gson()
            val recipesJson = sharedPreferences.getString("recipes", "[]")
            val type = object : TypeToken<ArrayList<FavoriteRecipesActivity.RecipeData>>() {}.type
            val recipes: ArrayList<FavoriteRecipesActivity.RecipeData> = gson.fromJson(recipesJson, type)

            val isFavorite = recipes.any { it.id == recipeId }
            bookmark.setImageResource(if (isFavorite) R.drawable.icon_bookmark_added else R.drawable.icon_bookmark)
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