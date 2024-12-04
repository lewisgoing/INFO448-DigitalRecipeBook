//package edu.uw.ischool.jtay25.digitalrecipebook
//
//import android.content.Intent
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.ImageView
//import android.widget.TextView
//import androidx.activity.enableEdgeToEdge
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.view.ViewCompat
//import androidx.core.view.WindowInsetsCompat
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.google.android.material.bottomnavigation.BottomNavigationView
//
//class FavoriteRecipesActivity : AppCompatActivity() {
//
//    private lateinit var navBar : BottomNavigationView
//
//    private inner class RecipeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
//        val titleText: TextView = view.findViewById(R.id.recipeTitle)
//        val chefText: TextView = view.findViewById(R.id.chefName)
//        val durationText: TextView = view.findViewById(R.id.duration)
//        val ratingText: TextView = view.findViewById(R.id.ratingText)
//        val recipeImage: ImageView = view.findViewById(R.id.recipeImage)
//    }
//
//    private inner class RecipeAdapter : RecyclerView.Adapter<RecipeViewHolder>() {
//        private val titles = arrayOf(
//            "Traditional Spare Ribs",
//            "Spice Roasted Chicken",
//            "Spicy Fried Rice With Chicken Bali",
//            "Lamb Chops"
//        )
//        private val chefs = arrayOf("Chef John", "Mark Kelvin", "Spicy Nelly", "Chef Maria")
//        private val ratings = arrayOf("4.0", "4.0", "4.0", "3.0")
//        private val images = arrayOf(
//            R.drawable.ribs,
//            R.drawable.chicken,
//            R.drawable.fried_rice,
//            R.drawable.lamb_chops
//        )
//
//        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
//            val view = LayoutInflater.from(parent.context)
//                .inflate(R.layout.item_recipe_card, parent, false)
//            return RecipeViewHolder(view)
//        }
//
//        override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
//            holder.apply {
//                titleText.text = titles[position]
//                chefText.text = "By ${chefs[position]}"
//                durationText.text = "20 min"
//                ratingText.text = ratings[position]
//                recipeImage.setImageResource(images[position])
//            }
//        }
//
//        override fun getItemCount() = titles.size
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_favorite_recipes)
//
//        val recyclerView = findViewById<RecyclerView>(R.id.recipesRecyclerView)
//        recyclerView.layoutManager = LinearLayoutManager(this)
//        recyclerView.adapter = RecipeAdapter()
//
//        navBar = findViewById(R.id.bottomNavigationView)
//        navBar.setSelectedItemId(R.id.favorite)
//
//        navBar.setOnItemSelectedListener{ item ->
//            when(item.itemId) {
//                R.id.home -> {
//                    startActivity(Intent(this, MainActivity::class.java))
//                    true
//                }
//                R.id.favorite -> {
//                    true
//                }
//                //R.id.add -> startActivity(Intent(this, AddRecipeActivity::class.java))
//                else -> false
//            }
//        }
//
//
//    }
//}

package edu.uw.ischool.jtay25.digitalrecipebook

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

class FavoriteRecipesActivity : AppCompatActivity() {

    private lateinit var navBar: BottomNavigationView

    private inner class RecipeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleText: TextView = view.findViewById(R.id.recipeTitle)
        val chefText: TextView = view.findViewById(R.id.chefName)
        val durationText: TextView = view.findViewById(R.id.duration)
        val ratingText: TextView = view.findViewById(R.id.ratingText)
        val recipeImage: ImageView = view.findViewById(R.id.recipeImage)
    }

    private inner class RecipeAdapter : RecyclerView.Adapter<RecipeViewHolder>() {
        private val titles = arrayOf(
            "Traditional Spare Ribs",
            "Spice Roasted Chicken",
            "Spicy Fried Rice With Chicken Bali",
            "Lamb Chops"
        )
        private val chefs = arrayOf("Chef John", "Mark Kelvin", "Spicy Nelly", "Chef Maria")
        private val ratings = arrayOf("4.0", "4.0", "4.0", "3.0")
        private val images = arrayOf(
            R.drawable.ribs,
            R.drawable.chicken,
            R.drawable.fried_rice,
            R.drawable.lamb_chops
        )

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_recipe_card, parent, false)
            return RecipeViewHolder(view)
        }

        override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
            holder.apply {
                titleText.text = titles[position]
                chefText.text = "By ${chefs[position]}"
                durationText.text = "20 min"
                ratingText.text = ratings[position]
                recipeImage.setImageResource(images[position])
            }
        }

        override fun getItemCount() = titles.size
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite_recipes)

        val recyclerView = findViewById<RecyclerView>(R.id.recipesRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = RecipeAdapter()
        recyclerView.setHasFixedSize(true)

        navBar = findViewById(R.id.bottomNavigationView)
        navBar.selectedItemId = R.id.favorite

        navBar.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    true
                }
                R.id.favorite -> true
                //R.id.add -> startActivity(Intent(this, AddRecipeActivity::class.java))
                else -> false
            }
        }
    }
}