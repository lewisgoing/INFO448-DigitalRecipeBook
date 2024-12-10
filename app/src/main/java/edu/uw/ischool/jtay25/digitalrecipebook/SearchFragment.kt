package edu.uw.ischool.jtay25.digitalrecipebook

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
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
    private val filteredRecipes = mutableListOf<Recipe>()
    private val database = FirebaseDatabase.getInstance()
    private val recipesRef = database.getReference("recipes")

    // current filter and search state
    private var currentCategory: String? = null
    private var currentDuration: String? = null
    private var currentSearchQuery: String = ""

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
        return inflater.inflate(R.layout.search_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        try {
            setupViews(view)
            setupSearchListeners()
            setupFilterButton(view)
        } catch (e: Exception) {
            Log.e("SearchFragment", "Error in onViewCreated", e)
        }
    }

    private fun setupViews(view: View) {
        searchView = view.findViewById(R.id.searchView)
        recyclerView = view.findViewById(R.id.searchResultsRecyclerView)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = RecipeAdapter()
        recyclerView.adapter = adapter
        recyclerView.visibility = View.GONE
    }

    private fun setupFilterButton(view: View) {
        val filterButton = view.findViewById<ImageButton>(R.id.filterButton)
        filterButton.setOnClickListener {
            showFilterDialog()
        }
    }

    private fun showFilterDialog() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.search_filters, null)
        val categorySpinner = dialogView.findViewById<Spinner>(R.id.categorySpinner)
        val durationSpinner = dialogView.findViewById<Spinner>(R.id.durationSpinner)

        // Setup category spinner
        val categories = arrayOf("All Categories", "Breakfast", "Lunch", "Dinner", "Appetizers", "Dessert")
        val categoryAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = categoryAdapter

        // Setup duration spinner
        val durations = arrayOf("Any Time", "Under 30 mins", "30-60 mins", "Over 60 mins")
        val durationAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, durations)
        durationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        durationSpinner.adapter = durationAdapter

        val currentCategoryIndex = when (currentCategory) {
            "Breakfast" -> 1
            "Lunch" -> 2
            "Dinner" -> 3
            "Appetizers" -> 4
            "Dessert" -> 5
            else -> 0
        }
        categorySpinner.setSelection(currentCategoryIndex)

        val currentDurationIndex = when (currentDuration) {
            "Under 30 mins" -> 1
            "30-60 mins" -> 2
            "Over 60 mins" -> 3
            else -> 0
        }
        durationSpinner.setSelection(currentDurationIndex)

        AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setPositiveButton("Apply") { dialog, _ ->
                val selectedCategory = when (categorySpinner.selectedItemPosition) {
                    0 -> null
                    1 -> "Breakfast"
                    2 -> "Lunch"
                    3 -> "Dinner"
                    4 -> "Appetizers"
                    5 -> "Dessert"
                    else -> null
                }

                val selectedDuration = when (durationSpinner.selectedItemPosition) {
                    0 -> null
                    1 -> "Under 30 mins"
                    2 -> "30-60 mins"
                    3 -> "Over 60 mins"
                    else -> null
                }

                currentCategory = selectedCategory
                currentDuration = selectedDuration
                applyFiltersAndSearch()
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun setupSearchListeners() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                currentSearchQuery = query ?: ""
                applyFiltersAndSearch()
                hideKeyboard()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                currentSearchQuery = newText ?: ""
                applyFiltersAndSearch()
                return true
            }
        })

        searchView.setOnSearchClickListener {
            if (allRecipes.isEmpty()) {
                loadRecipesFromFirebase()
            }
            showSearchUI()
        }

        searchView.setOnCloseListener {
            hideKeyboard()
            hideSearchUI()
            true
        }

        val searchIcon = searchView.findViewById<ImageView>(androidx.appcompat.R.id.search_button)
        searchIcon.setOnClickListener {
            hideKeyboard()
            searchView.isIconified = false
        }

        searchView.isIconified = true
    }

    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    private fun applyFiltersAndSearch() {
        filteredRecipes.clear()

        var filtered = if (currentCategory != null) {
            allRecipes.filter { it.category == currentCategory }
        } else {
            allRecipes
        }

        filtered = when (currentDuration) {
            "Under 30 mins" -> filtered.filter {
                val minutes = parseMinutes(it.duration)
                minutes in 0..29
            }
            "30-60 mins" -> filtered.filter {
                val minutes = parseMinutes(it.duration)
                minutes in 30..60
            }
            "Over 60 mins" -> filtered.filter {
                val minutes = parseMinutes(it.duration)
                minutes > 60
            }
            else -> filtered
        }

        // Apply search query
        filtered = if (currentSearchQuery.isNotBlank()) {
            filtered.filter { recipe ->
                recipe.title.contains(currentSearchQuery, ignoreCase = true) ||
                        recipe.chef.contains(currentSearchQuery, ignoreCase = true)
            }
        } else {
            filtered
        }

        filteredRecipes.addAll(filtered)
        recyclerView.visibility = View.VISIBLE
        adapter.notifyDataSetChanged()

        Log.d("SearchFragment", "Applied filters - Category: $currentCategory, Duration: $currentDuration, Search: $currentSearchQuery, Results: ${filteredRecipes.size}")
    }

    private fun parseMinutes(duration: String): Int {
        val durationLower = duration.toLowerCase()
        return when {
            durationLower.contains("hour") && durationLower.contains("min") -> {
                val hours = """(\d+)\s*hour""".toRegex().find(durationLower)?.groupValues?.get(1)?.toIntOrNull() ?: 0
                val mins = """(\d+)\s*min""".toRegex().find(durationLower)?.groupValues?.get(1)?.toIntOrNull() ?: 0
                hours * 60 + mins
            }
            durationLower.contains("hour") -> {
                val hours = """(\d+)\s*hour""".toRegex().find(durationLower)?.groupValues?.get(1)?.toIntOrNull() ?: 0
                hours * 60
            }
            durationLower.contains("min") -> {
                """(\d+)\s*min""".toRegex().find(durationLower)?.groupValues?.get(1)?.toIntOrNull() ?: 0
            }
            else -> 0
        }
    }

    private fun loadRecipesFromFirebase() {
        Log.d("SearchFragment", "Loading recipes from Firebase")
        recipesRef.get().addOnSuccessListener { snapshot ->
            allRecipes.clear()
            snapshot.children.forEach { recipeSnapshot ->
                try {
                    val recipe = Recipe(
                        id = recipeSnapshot.key ?: "",
                        title = recipeSnapshot.child("title").getValue(String::class.java) ?: "",
                        chef = recipeSnapshot.child("chef").getValue(String::class.java) ?: "",
                        duration = recipeSnapshot.child("duration").getValue(String::class.java) ?: "",
                        category = recipeSnapshot.child("category").getValue(String::class.java) ?: ""
                    )
                    allRecipes.add(recipe)
                } catch (e: Exception) {
                    Log.e("SearchFragment", "Error parsing recipe", e)
                }
            }
            applyFiltersAndSearch()
        }.addOnFailureListener { e ->
            Log.e("SearchFragment", "Error loading recipes", e)
            Toast.makeText(context, "Failed to load recipes: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showSearchUI() {
        view?.setBackgroundColor(requireContext().getColor(android.R.color.white))
        requireActivity().findViewById<ConstraintLayout>(R.id.frameLayout)?.visibility = View.GONE

        val params = recyclerView.layoutParams as ConstraintLayout.LayoutParams
        params.height = ConstraintLayout.LayoutParams.MATCH_PARENT
        recyclerView.layoutParams = params

        recyclerView.visibility = View.VISIBLE

        if (allRecipes.isEmpty()) {
            loadRecipesFromFirebase()
        } else {
            applyFiltersAndSearch()
        }
    }

    private fun hideSearchUI() {
        view?.setBackgroundColor(requireContext().getColor(android.R.color.transparent))
        recyclerView.visibility = View.GONE
        requireActivity().findViewById<ConstraintLayout>(R.id.frameLayout)?.visibility = View.VISIBLE
        currentSearchQuery = ""
        applyFiltersAndSearch()
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
            holder.apply {
                titleText.text = recipe.title
                chefText.text = getString(R.string.chef_name_format, recipe.chef)
                durationText.text = recipe.duration
                recipeImage.setImageResource(R.drawable.baseline_add_24)
            }
        }

        override fun getItemCount() = filteredRecipes.size
    }

    override fun onResume() {
        super.onResume()
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (searchView.query.isEmpty()) {
                searchView.isIconified = true
            } else {
                searchView.setQuery("", false)
            }
        }
    }

    companion object {
        fun newInstance() = SearchFragment()
    }
}