package edu.uw.ischool.jtay25.digitalrecipebook

// get rid of intent
import android.content.Intent
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        FirebaseApp.initializeApp(this)
        recyclerView = findViewById(R.id.cardView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = RecipeAdapter(recipeList) { recipe ->
            navigateToRecipeDetails(recipe)
        }
        recyclerView.adapter = adapter
// get rid of intent
//        supportFragmentManager.beginTransaction()
//            .replace(R.id.searchContainer, SearchFragment.newInstance())
//            .commit()

        navBar = findViewById(R.id.bottomNavigationView)
        navBar.setSelectedItemId(R.id.home)

        navBar.setOnItemSelectedListener{ item ->
            when(item.itemId) {
                R.id.home -> {
                    true
                }
                R.id.favorite -> {
                    startActivity(Intent(this, FavoriteRecipesActivity::class.java))
                    true
                }
                R.id.nav_add_recipe -> {
                    Log.d("Navigation", "Add Recipe clicked")
                    val intent = Intent(this, AddRecipeActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }

        findViewById<ImageButton>(R.id.imageButton2).setOnClickListener { filterRecipes("Breakfast") }
        findViewById<ImageButton>(R.id.imageButton3).setOnClickListener { filterRecipes("Lunch") }
        findViewById<ImageButton>(R.id.imageButton4).setOnClickListener { filterRecipes("Dinner") }
        findViewById<ImageButton>(R.id.imageButton5).setOnClickListener { filterRecipes("Appetizers") }
        findViewById<ImageButton>(R.id.imageButton6).setOnClickListener { filterRecipes("Dessert") }

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


