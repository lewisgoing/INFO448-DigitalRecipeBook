package edu.uw.ischool.jtay25.digitalrecipebook

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.getValue
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class RecipeDetailsActivity : AppCompatActivity() {

    private lateinit var titleTextView: TextView
    private lateinit var chefTextView: TextView
    private lateinit var categoryTextView: TextView
    private lateinit var durationTextView: TextView
    private lateinit var instructionsTextView: TextView
    private lateinit var ingredientsTextView: TextView
    private lateinit var bookmarkIcon: ImageView
    private lateinit var backArrow: ImageButton

    private lateinit var recipeId: String
    private lateinit var currentRecipe: Recipe

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_details)

        // Initialize views
        titleTextView = findViewById(R.id.titleTextView)
        chefTextView = findViewById(R.id.chefTextView)
        categoryTextView = findViewById(R.id.categoryTextView)
        durationTextView = findViewById(R.id.durationTextView)
        instructionsTextView = findViewById(R.id.instructionsTextView)
        ingredientsTextView = findViewById(R.id.ingredientsTextView)
        bookmarkIcon = findViewById(R.id.bookmarkIcon)
        backArrow = findViewById(R.id.backArrow)

        // Get the recipe ID from the intent
        recipeId = intent.getStringExtra("RECIPE_ID") ?: ""
        if (recipeId.isEmpty()) {
            Toast.makeText(this, "Invalid Recipe ID", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            fetchRecipeDetails(recipeId)
        }

        bookmarkIcon.setOnClickListener {
            toggleFavoriteStatus()
        }

        backArrow.setOnClickListener { onBackPressed() }
    }

    private fun fetchRecipeDetails(recipeId: String) {
        val databaseRef = FirebaseDatabase.getInstance().getReference("recipes").child(recipeId)

        databaseRef.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val title = snapshot.child("title").getValue(String::class.java)
                val chef = snapshot.child("chef").getValue(String::class.java)
                val category = snapshot.child("category").getValue(String::class.java)
                val duration = snapshot.child("duration").getValue(String::class.java)
                val instructions = snapshot.child("instructions").getValue<List<String>>()
                val ingredients = snapshot.child("ingredients").getValue<List<String>>()
                val tag = snapshot.child("tag").getValue(Boolean::class.java) ?: false

                currentRecipe = Recipe(
                    id = recipeId,
                    title = title ?: "N/A",
                    chef = chef ?: "N/A",
                    duration = duration ?: "N/A",
                    ingredients = ingredients ?: emptyList(),
                    instructions = instructions ?: emptyList(),
                    category = category ?: "N/A",
                    tag = tag
                )

                // Update UI
                titleTextView.text = currentRecipe.title
                chefTextView.text = "Chef: ${currentRecipe.chef}"
                categoryTextView.text = "Category: ${currentRecipe.category}"
                durationTextView.text = "Duration: ${currentRecipe.duration}"
                ingredientsTextView.text = currentRecipe.ingredients.joinToString("\n\n")
                instructionsTextView.text = currentRecipe.instructions.joinToString("\n\n")
                updateBookmarkIcon(tag)
            } else {
                Toast.makeText(this, "Recipe not found", Toast.LENGTH_SHORT).show()
                finish()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to load recipe", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun toggleFavoriteStatus() {
        val sharedPreferences = getSharedPreferences("RecipePrefs", Context.MODE_PRIVATE)
        val gson = Gson()
        val recipesJson = sharedPreferences.getString("recipes", "[]")
        val type = object : TypeToken<ArrayList<FavoriteRecipesActivity.RecipeData>>() {}.type
        val favoriteRecipes: ArrayList<FavoriteRecipesActivity.RecipeData> =
            gson.fromJson(recipesJson, type)

        val isFavorite = favoriteRecipes.any { it.id == currentRecipe.id }
        if (isFavorite) {
            favoriteRecipes.removeAll { it.id == currentRecipe.id }
            bookmarkIcon.setImageResource(R.drawable.icon_bookmark_filled)
            Toast.makeText(this, "Removed from favorites", Toast.LENGTH_SHORT).show()
        } else {
            favoriteRecipes.add(
                FavoriteRecipesActivity.RecipeData(
                    id = currentRecipe.id,
                    title = currentRecipe.title,
                    chef = currentRecipe.chef,
                    duration = currentRecipe.duration
                )
            )
            bookmarkIcon.setImageResource(R.drawable.icon_bookmark_added)
            Toast.makeText(this, "Added to favorites", Toast.LENGTH_SHORT).show()
        }

        // Save changes locally
        val editor = sharedPreferences.edit()
        editor.putString("recipes", gson.toJson(favoriteRecipes))
        editor.apply()

        // Update Firebase
        val tagRef = FirebaseDatabase.getInstance().getReference("recipes")
            .child(currentRecipe.id)
            .child("tag")
        tagRef.setValue(!isFavorite)
    }

    private fun updateBookmarkIcon(isFavorite: Boolean) {
        bookmarkIcon.setImageResource(
            if (isFavorite) R.drawable.icon_bookmark_added else R.drawable.icon_bookmark_filled
        )
    }
}
