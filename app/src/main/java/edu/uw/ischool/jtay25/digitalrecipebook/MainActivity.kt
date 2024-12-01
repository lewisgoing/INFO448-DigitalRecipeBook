package edu.uw.ischool.jtay25.digitalrecipebook

// get rid of intent
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var navBar : BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        supportFragmentManager.beginTransaction()
            .replace(R.id.searchContainer, SearchFragment.newInstance())
            .commit()

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
                //R.id.add -> startActivity(Intent(this, AddRecipeActivity::class.java))
                else -> false
            }
        }



    }

    private fun showSearchFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayout, SearchFragment.newInstance())
            .addToBackStack(null)
            .commit()
    }


}