package edu.uw.ischool.jtay25.digitalrecipebook

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase

class SearchFragment : Fragment() {
    private lateinit var searchView: SearchView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RecipeAdapter
    private val allRecipes = mutableListOf<Recipe>()
    private var filteredRecipes = mutableListOf<Recipe>()
    private val database = FirebaseDatabase.getInstance()
    private val recipesRef = database.getReference("recipes")

    data class Recipe(
        val id: String = "",
        val title: String = "",
        val chef: String = "",
        val duration: String = "",
        val category: String = "",
        val ingredients: List<String> = listOf(),
        val instructions: List<String> = listOf(),
        val tag: Boolean = false
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("SearchFragment", "onCreateView called")
        return inflater.inflate(R.layout.search_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("SearchFragment", "onViewCreated called")

        try {
            searchView = view.findViewById(R.id.searchView)
            recyclerView = view.findViewById(R.id.searchResultsRecyclerView)

            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            adapter = RecipeAdapter()
            recyclerView.adapter = adapter
            recyclerView.visibility = View.GONE

            setupSearchListeners()
            setupFilterButton(view)

            Log.d("SearchFragment", "Setup completed successfully")
        } catch (e: Exception) {
            Log.e("SearchFragment", "Error in onViewCreated", e)
        }
    }

    private fun setupSearchListeners() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { performSearch(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { performSearch(it) }
                return true
            }
        })

        searchView.setOnSearchClickListener {
            Log.d("SearchFragment", "Search clicked")
            // Load recipes only when search is clicked
            if (allRecipes.isEmpty()) {
                loadRecipesFromFirebase()
            }
            showSearchUI()
        }

        searchView.setOnCloseListener {
            hideSearchUI()
            true
        }

        // Start with search view iconified (collapsed)
        searchView.isIconified = true
    }

    private fun setupFilterButton(view: View) {
        val filterButton = view.findViewById<ImageButton>(R.id.filterButton)
        filterButton.setOnClickListener {
            val dialogView = LayoutInflater.from(context).inflate(R.layout.search_filters, null)
            val categorySpinner = dialogView.findViewById<Spinner>(R.id.categorySpinner)

            // Setup category spinner
            val categories = arrayOf("All Categories", "Breakfast", "Lunch", "Dinner", "Appetizers", "Dessert")
            val categoryAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
            categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            categorySpinner.adapter = categoryAdapter

            val dialog = AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .setPositiveButton("Apply") { dialog, _ ->
                    val selectedCategory = when(categorySpinner.selectedItemPosition) {
                        0 -> null
                        1 -> "Breakfast"
                        2 -> "Lunch"
                        3 -> "Dinner"
                        4 -> "Appetizers"
                        5 -> "Dessert"
                        else -> null
                    }

                    // Filter recipes by category
                    filteredRecipes.clear()
                    filteredRecipes.addAll(
                        if (selectedCategory == null) allRecipes
                        else allRecipes.filter { it.category == selectedCategory }
                    )
                    adapter.notifyDataSetChanged()
                    dialog.dismiss()
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
                .create()

            dialog.show()
        }
    }


    private fun loadRecipesFromFirebase() {
        Log.d("SearchFragment", "Starting to load recipes from Firebase")
        recipesRef.get().addOnSuccessListener { snapshot ->
            Log.d("SearchFragment", "Firebase snapshot received")
            allRecipes.clear()
            snapshot.children.forEach { recipeSnapshot ->
                try {
                    Log.d("SearchFragment", "Processing recipe: ${recipeSnapshot.key}")
                    val recipe = Recipe(
                        id = recipeSnapshot.key ?: "",
                        title = recipeSnapshot.child("title").getValue(String::class.java) ?: "",
                        chef = recipeSnapshot.child("chef").getValue(String::class.java) ?: "",
                        duration = recipeSnapshot.child("duration").getValue(String::class.java) ?: "",
                        category = recipeSnapshot.child("category").getValue(String::class.java) ?: ""
                    )
                    Log.d("SearchFragment", "Added recipe: ${recipe.title}")
                    allRecipes.add(recipe)
                } catch (e: Exception) {
                    Log.e("SearchFragment", "Error parsing recipe", e)
                }
            }
            Log.d("SearchFragment", "Total recipes loaded: ${allRecipes.size}")
            // Show all recipes after loading
            filteredRecipes.clear()
            filteredRecipes.addAll(allRecipes)
            adapter.notifyDataSetChanged()
        }.addOnFailureListener { e ->
            Log.e("SearchFragment", "Error loading recipes", e)
            Toast.makeText(context, "Failed to load recipes: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }



    private fun hideSearchUI() {
        view?.setBackgroundColor(requireContext().getColor(android.R.color.transparent))
        recyclerView.visibility = View.GONE
        // Update to use correct layout ID and type
        requireActivity().findViewById<ConstraintLayout>(R.id.frameLayout)?.visibility = View.VISIBLE
        filteredRecipes.clear()
        adapter.notifyDataSetChanged()
    }


    private fun performSearch(query: String) {
        Log.d("SearchFragment", "Performing search for: $query")
        filteredRecipes.clear()

        if (query.isBlank()) {
            filteredRecipes.addAll(allRecipes)
            Log.d("SearchFragment", "Showing all ${allRecipes.size} recipes")
        } else {
            val newRecipes = allRecipes.filter { recipe ->
                val matches = recipe.title.contains(query, ignoreCase = true) ||
                        recipe.chef.contains(query, ignoreCase = true)
                if (matches) {
                    Log.d("SearchFragment", "Found matching recipe: ${recipe.title}")
                }
                matches
            }
            filteredRecipes.addAll(newRecipes)
            Log.d("SearchFragment", "Found ${newRecipes.size} matching recipes")
        }

        recyclerView.visibility = View.VISIBLE
        adapter.notifyDataSetChanged()
    }

    private fun showSearchUI() {
        Log.d("SearchFragment", "Showing search UI")
        view?.setBackgroundColor(requireContext().getColor(android.R.color.white))
        requireActivity().findViewById<ConstraintLayout>(R.id.frameLayout)?.visibility = View.GONE

        // Set RecyclerView height to match parent
        val params = recyclerView.layoutParams as ConstraintLayout.LayoutParams
        params.height = ConstraintLayout.LayoutParams.MATCH_PARENT
        recyclerView.layoutParams = params

        recyclerView.visibility = View.VISIBLE

        // Load initial data if needed
        if (allRecipes.isEmpty()) {
            loadRecipesFromFirebase()
        } else {
            filteredRecipes.clear()
            filteredRecipes.addAll(allRecipes)
            adapter.notifyDataSetChanged()
        }
    }

    private inner class RecipeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleText: TextView = view.findViewById(R.id.recipeTitle)
        val chefText: TextView = view.findViewById(R.id.chefName)
        val durationText: TextView = view.findViewById(R.id.duration)
        val recipeImage: ImageView = view.findViewById(R.id.recipeImage)

        init {
            view.setOnClickListener {
                val recipe = filteredRecipes[adapterPosition]
                val intent = Intent(requireContext(), RecipeDetailsActivity::class.java)
                intent.putExtra("RECIPE_ID", recipe.id)
                startActivity(intent)
            }
        }
    }

    private inner class RecipeAdapter : RecyclerView.Adapter<RecipeViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_recipe_card, parent, false)
            return RecipeViewHolder(view)
        }

        override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
            val recipe = filteredRecipes[position]
            Log.d("SearchFragment", "Binding recipe at position $position: ${recipe.title}")
            holder.apply {
                titleText.text = recipe.title
                chefText.text = getString(R.string.chef_name_format, recipe.chef)
                durationText.text = recipe.duration
                // Set a default image
                recipeImage.setImageResource(R.drawable.baseline_add_24)
            }
        }
        override fun getItemCount() = filteredRecipes.size
    }

    override fun onResume() {
        super.onResume()
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (searchView.query.isEmpty()) {
                searchView.isIconified = true  // This will collapse the SearchView
            } else {
                searchView.setQuery("", false)  // Clear the query
            }
        }
    }

    companion object {
        fun newInstance() = SearchFragment()
    }
}