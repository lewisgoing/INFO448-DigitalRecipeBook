package edu.uw.ischool.jtay25.digitalrecipebook

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class FavoriteRecipesActivity : AppCompatActivity() {

    private lateinit var navBar: BottomNavigationView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RecipeAdapter

    data class RecipeData(
        val id: String,
        val title: String,
        val chef: String,
        val duration: String
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite_recipes)

        setupRecyclerView()
        setupNavBar()
    }

    private fun setupRecyclerView() {
        recyclerView = findViewById(R.id.recipesRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = RecipeAdapter(getRecipesFromSharedPreferences())
        recyclerView.adapter = adapter
        recyclerView.setHasFixedSize(true)
    }

    private fun setupNavBar() {
        navBar = findViewById(R.id.bottomNavigationView)
        navBar.selectedItemId = R.id.favorite

        navBar.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    true
                }
                R.id.nav_add_recipe -> {
                    startActivity(Intent(this, AddRecipeActivity::class.java))
                    true
                }
                R.id.favorite -> true
                else -> false
            }
        }
    }

    private inner class RecipeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleText: TextView = view.findViewById(R.id.recipeTitle)
        val chefText: TextView = view.findViewById(R.id.chefName)
        val durationText: TextView = view.findViewById(R.id.duration)
        val bookmarkIcon: ImageView = view.findViewById(R.id.bookmarkIcon)

        init {
            view.setOnClickListener {
                val recipe = adapter.recipes[adapterPosition]
                val intent = Intent(this@FavoriteRecipesActivity, RecipeDetailsActivity::class.java)
                intent.putExtra("RECIPE_ID", recipe.id)
                startActivity(intent)
            }
        }
    }

    private inner class RecipeAdapter(var recipes: ArrayList<RecipeData>) : RecyclerView.Adapter<RecipeViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_recipe_card, parent, false)
            return RecipeViewHolder(view)
        }

        override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
            val recipe = recipes[position]
            holder.apply {
                titleText.text = recipe.title
                chefText.text = "By ${recipe.chef}"
                durationText.text = recipe.duration
                bookmarkIcon.setImageResource(R.drawable.icon_bookmark_added)

                bookmarkIcon.setOnClickListener {
                    removeRecipe(recipe.id)
                    notifyItemRemoved(position)
                }
            }
        }

        override fun getItemCount() = recipes.size

        fun removeRecipe(id: String) {
            val index = recipes.indexOfFirst { it.id == id }
            if (index != -1) {
                recipes.removeAt(index)
                removeRecipeFromSharedPreferences(id)
            }
        }
    }

    private fun getRecipesFromSharedPreferences(): ArrayList<RecipeData> {
        val sharedPreferences = getSharedPreferences("RecipePrefs", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString("recipes", null)
        val type = object : TypeToken<ArrayList<RecipeData>>() {}.type
        return gson.fromJson(json, type) ?: ArrayList()
    }

    private fun removeRecipeFromSharedPreferences(id: String) {
        val recipes = getRecipesFromSharedPreferences()
        recipes.removeAll { it.id == id }
        saveRecipesToSharedPreferences(recipes)
    }

    private fun saveRecipesToSharedPreferences(recipes: List<RecipeData>) {
        val sharedPreferences = getSharedPreferences("RecipePrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(recipes)
        editor.putString("recipes", json)
        editor.apply()
    }
}