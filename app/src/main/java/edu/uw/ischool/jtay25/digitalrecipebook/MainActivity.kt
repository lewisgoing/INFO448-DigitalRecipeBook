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

    private val db = FirebaseFirestore.getInstance()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RecipeAdapter
    private var recipeList = mutableListOf<Recipe>()
    private lateinit var navBar : BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        FirebaseApp.initializeApp(this)
        recyclerView = findViewById(R.id.cardView)
        adapter = RecipeAdapter(recipeList) { recipe ->
            val intent = Intent(this, RecipeDetailsFragment::class.java)
            intent.putExtra("RECIPE_ID", recipe.id)
            startActivity(intent)
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
// get rid of intent
//        supportFragmentManager.beginTransaction()
//            .replace(R.id.searchContainer, SearchFragment.newInstance())
//            .commit()

        //supportFragmentManager.beginTransaction().replace(R.id.frameLayout, CategoriesFragment()).commit()

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

    private fun filterRecipes(category: String) {
        db.collection("recipes")
            .whereEqualTo("category", category) // Filter recipes by category
            .get()
            .addOnSuccessListener { snapshot ->
                recipeList.clear()
                for (doc in snapshot.documents) {
                    val recipe = doc.toObject(Recipe::class.java)
                    if (recipe != null) recipeList.add(recipe)
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


