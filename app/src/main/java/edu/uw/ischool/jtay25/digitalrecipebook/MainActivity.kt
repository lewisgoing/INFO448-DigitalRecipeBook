package edu.uw.ischool.jtay25.digitalrecipebook

// get rid of intent
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RecipeAdapter
    private var recipeList = mutableListOf<Recipe>()
    private lateinit var navBar : BottomNavigationView


    private lateinit var breakfast : ImageButton
    private lateinit var lunch : ImageButton
    private lateinit var dinner : ImageButton
    private lateinit var appetizer : ImageButton
    private lateinit var dessert : ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        FirebaseApp.initializeApp(this)

        recyclerView = findViewById(R.id.cardView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = RecipeAdapter(recipeList) { recipe ->
            navigateToRecipeDetails(recipe)
        }
        recyclerView.adapter = adapter

        setupNavigation()
        setupSearch()
    }

    private fun setupNavigation() {
        navBar = findViewById(R.id.bottomNavigationView)
        navBar.selectedItemId = R.id.home

        val navBar = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        navBar.setSelectedItemId(R.id.home)

        navBar.setOnItemSelectedListener{ item ->
            when(item.itemId) {
                R.id.home -> true
                R.id.favorite -> {
                    startActivity(Intent(this, FavoriteRecipesActivity::class.java))
                    true
                }
                else -> false
            }
        }

        val addRecipe = findViewById<FloatingActionButton>(R.id.addRecipe)

        addRecipe.setOnClickListener {
            Log.d("Navigation", "Add Recipe clicked")
            val intent = Intent(this, AddRecipeActivity::class.java)
            startActivity(intent)
        }

        breakfast = findViewById<ImageButton>(R.id.imageButton1)
        lunch = findViewById<ImageButton>(R.id.imageButton2)
        dinner = findViewById<ImageButton>(R.id.imageButton3)
        appetizer = findViewById<ImageButton>(R.id.imageButton4)
        dessert = findViewById<ImageButton>(R.id.imageButton5)
        var oldSelection = ""
        breakfast.setOnClickListener {
            val newSelection = "Breakfast"
            filterRecipes(newSelection)
            updateCategorySelection(newSelection, oldSelection)
            oldSelection = newSelection
        }
        lunch.setOnClickListener {
            val newSelection = "Lunch"
            filterRecipes(newSelection)
            updateCategorySelection(newSelection, oldSelection)
            oldSelection = newSelection
        }
        dinner.setOnClickListener {
            val newSelection = "Dinner"
            filterRecipes(newSelection)
            updateCategorySelection(newSelection, oldSelection)
            oldSelection = newSelection
        }
        appetizer.setOnClickListener {
            val newSelection = "Appetizers"
            filterRecipes(newSelection)
            updateCategorySelection(newSelection, oldSelection)
            oldSelection = newSelection
        }
        dessert.setOnClickListener {
            val newSelection = "Dessert"
            filterRecipes(newSelection)
            updateCategorySelection(newSelection, oldSelection)
            oldSelection = newSelection
        }
    }



    private fun setupSearch() {
        // Add search fragment
        supportFragmentManager.beginTransaction()
            .replace(R.id.searchContainer, SearchFragment.newInstance())
            .commit()
    }
    //    private fun showSearchFragment() {
//        supportFragmentManager.beginTransaction()
//            .replace(R.id.frameLayout, SearchFragment.newInstance())
//            .addToBackStack(null)
//            .commit()
//    }
    private fun navigateToRecipeDetails(recipe: Recipe) {
        // Navigate to RecipeDetailsActivity with recipe ID
        val intent = Intent(this, RecipeDetailsActivity::class.java)
        intent.putExtra("RECIPE_ID", recipe.id)
        startActivity(intent)
    }

    private fun filterRecipes(category: String) {
        val db = com.google.firebase.database.FirebaseDatabase.getInstance()
        val recipesRef = db.getReference("recipes")

        recipesRef.orderByChild("category").equalTo(category)
            .get()
            .addOnSuccessListener { snapshot ->
                recipeList.clear()
                for (childSnapshot in snapshot.children) {
                    val recipe = childSnapshot.getValue(Recipe::class.java)
                    if (recipe != null) recipeList.add(recipe)
                }
                if (recipeList.isEmpty()) {
                    // Notify user that no recipes were found
                    Toast.makeText(this, "No recipes found for $category", Toast.LENGTH_SHORT).show()
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to load recipes: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateCategorySelection(newSelection: String, oldSelection: String) {
        when(oldSelection) {
            "Breakfast" -> { breakfast.setImageResource(R.drawable.icon_breakfast) }
            "Lunch" -> { lunch.setImageResource(R.drawable.icon_lunch) }
            "Dinner" -> { dinner.setImageResource(R.drawable.icon_dinner) }
            "Appetizers" -> { appetizer.setImageResource(R.drawable.icon_appetizer) }
            "Dessert" -> { dessert.setImageResource(R.drawable.icon_dessert) }
            else -> {}
        }
        when(newSelection) {
            "Breakfast" -> { breakfast.setImageResource(R.drawable.icon_breakfast_selected) }
            "Lunch" -> { lunch.setImageResource(R.drawable.icon_lunch_selected) }
            "Dinner" -> { dinner.setImageResource(R.drawable.icon_dinner_selected) }
            "Appetizers" -> { appetizer.setImageResource(R.drawable.icon_appetizer_selected) }
            "Dessert" -> { dessert.setImageResource(R.drawable.icon_dessert_selected) }
        }
    }


//    private fun loadRecipes() {
//        db.collection("recipes")
//            .get()
//            .addOnSuccessListener { snapshot ->
//                recipeList.clear()
//                for (doc in snapshot.documents) {
//                    val recipe = doc.toObject(Recipe::class.java)
//                    if (recipe != null) recipeList.add(recipe)
//                }
//                adapter.notifyDataSetChanged()
//            }
//            .addOnFailureListener { e ->
//                Toast.makeText(this, "Failed to load recipes: ${e.message}", Toast.LENGTH_SHORT).show()
//            }
//    }
}