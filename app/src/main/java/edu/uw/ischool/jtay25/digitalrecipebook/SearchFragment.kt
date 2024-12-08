//package edu.uw.ischool.jtay25.digitalrecipebook
//
//import android.os.Bundle
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.*
//import androidx.activity.addCallback
//import androidx.appcompat.app.AlertDialog
//import androidx.appcompat.widget.SearchView
//import androidx.fragment.app.Fragment
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//
//class SearchFragment : Fragment() {
//    private lateinit var searchView: SearchView
//    private lateinit var recyclerView: RecyclerView
//    private lateinit var adapter: RecipeAdapter
//    private var currentCategory: String? = null
//    private var currentTimeFilter: String? = null
//
//    private val allRecipes = listOf(
//        Recipe("Traditional Spare Ribs", "Chef John", "20 min", "4.0", R.drawable.ribs, "Dinner"),
//        Recipe("Spice Roasted Chicken", "Mark Kelvin", "20 min", "4.0", R.drawable.chicken, "Dinner"),
//        Recipe("Spicy Fried Rice With Chicken Bali", "Spicy Nelly", "20 min", "4.0", R.drawable.fried_rice, "Dinner"),
//        Recipe("Lamb Chops", "Chef Maria", "20 min", "3.0", R.drawable.lamb_chops,"Dinner")
//    )
//
//    private var filteredRecipes = mutableListOf<Recipe>()
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        return inflater.inflate(R.layout.search_fragment, container, false)
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        searchView = view.findViewById(R.id.searchView)
//        recyclerView = view.findViewById(R.id.searchResultsRecyclerView)
//
//        recyclerView.layoutManager = LinearLayoutManager(requireContext())
//        adapter = RecipeAdapter()
//        recyclerView.adapter = adapter
//
//        /* listens for search query */
//        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
//            override fun onQueryTextSubmit(query: String?): Boolean {
//                query?.let { performSearch(it) }
//                return true
//            }
//
//            override fun onQueryTextChange(newText: String?): Boolean {
//                newText?.let { performSearch(it) }
//                return true
//            }
//        })
//
//        /* listens for search icon to be clicked */
//        searchView.setOnSearchClickListener {
//            Log.d("SearchFragment", "Search clicked")
//            view.setBackgroundColor(requireContext().getColor(android.R.color.white))
//
//            requireActivity().apply {
//                findViewById<Spinner>(R.id.dropdown)?.visibility = View.GONE
//                findViewById<TextView>(R.id.categoryTitle)?.visibility = View.GONE
//                findViewById<FrameLayout>(R.id.frameLayout)?.visibility = View.GONE
//            }
//
//            recyclerView.visibility = View.VISIBLE
//            val params = recyclerView.layoutParams
//            params.height = 1000
//            recyclerView.layoutParams = params
//
//            val oldSize = filteredRecipes.size
//            filteredRecipes.clear()
//            if (oldSize > 0) {
//                adapter.notifyItemRangeRemoved(0, oldSize)
//            }
//            filteredRecipes.addAll(allRecipes)
//            adapter.notifyItemRangeInserted(0, allRecipes.size)
//        }
//
//        /* listens for search to be closed */
//        searchView.setOnCloseListener {
//            val oldSize = filteredRecipes.size
//            filteredRecipes.clear()
//            if (oldSize > 0) {
//                adapter.notifyItemRangeRemoved(0, oldSize)
//            }
//
//            view.setBackgroundColor(requireContext().getColor(android.R.color.transparent))
//            recyclerView.visibility = View.GONE
//
//            requireActivity().apply {
//                findViewById<Spinner>(R.id.dropdown)?.visibility = View.VISIBLE
//                findViewById<TextView>(R.id.categoryTitle)?.visibility = View.VISIBLE
//                findViewById<FrameLayout>(R.id.frameLayout)?.visibility = View.VISIBLE
//            }
//            true
//        }
//
//        searchView.isIconified = true
//
//        /* filter button code */
//        val filterButton = view.findViewById<ImageButton>(R.id.filterButton)
//        filterButton.setOnClickListener {
//            setupFilterDialog()
//        }
//    }
//
//    // returns to category view of homepage via pressing back/removing search query
//    override fun onResume() {
//        super.onResume()
//        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
//            if (searchView.query.isEmpty()) {
//                searchView.isIconified = true
//            } else {
//                searchView.setQuery("", false)
//            }
//        }
//    }
//
//    private fun setupFilterDialog() {
//        val dialogView = LayoutInflater.from(context).inflate(R.layout.search_filters, null)
//
//        val categorySpinner = dialogView.findViewById<Spinner>(R.id.categorySpinner)
//        val categories = arrayOf("All Categories", "Breakfast", "Lunch", "Dinner", "Other")
//        val categoryAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
//        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        categorySpinner.adapter = categoryAdapter
//
//        val categoryIndex = when(currentCategory) {
//            null -> 0
//            "Breakfast" -> 1
//            "Lunch" -> 2
//            "Dinner" -> 3
//            "Other" -> 4
//            else -> 0
//        }
//        categorySpinner.setSelection(categoryIndex)
//
//        val timeGroup = dialogView.findViewById<RadioGroup>(R.id.timeGroup)
//        currentTimeFilter = when(timeGroup.checkedRadioButtonId) {
//            R.id.anyTime -> null
//            R.id.quick -> "Quick"
//            R.id.medium -> "Medium"
//            R.id.long_duration -> "Long"
//            else -> null
//        }
//
//        val dialog = AlertDialog.Builder(requireContext())
//            .setView(dialogView)
//            .setPositiveButton("Apply") { dialog, _ ->
//                currentCategory = when(categorySpinner.selectedItemPosition) {
//                    0 -> null
//                    1 -> "Breakfast"
//                    2 -> "Lunch"
//                    3 -> "Dinner"
//                    4 -> "Other"
//                    else -> null
//                }
//
//                currentTimeFilter = when(timeGroup.checkedRadioButtonId) {
//                    R.id.anyTime -> null
//                    R.id.quick -> "Quick"
//                    R.id.medium -> "Medium"
//                    R.id.long_duration -> "Long"
//                    else -> null
//                }
//
//                val oldSize = filteredRecipes.size
//                filteredRecipes.clear()
//                if (oldSize > 0) {
//                    adapter.notifyItemRangeRemoved(0, oldSize)
//                }
//
//                filteredRecipes.addAll(allRecipes.filter { recipe ->
//                    val matchesCategory = currentCategory == null || recipe.category == currentCategory
//                    val matchesTime = when(currentTimeFilter) {
//                        "Quick" -> recipe.durationInMinutes() <= 30
//                        "Medium" -> recipe.durationInMinutes() in 31..60
//                        "Long" -> recipe.durationInMinutes() > 60
//                        else -> true
//                    }
//                    matchesCategory && matchesTime
//                })
//
//                adapter.notifyItemRangeInserted(0, filteredRecipes.size)
//                dialog.dismiss()
//            }
//            .setNegativeButton("Cancel") { dialog, _ ->
//                dialog.dismiss()
//            }
//            .create()
//
//        dialog.show()
//    }
//
//    private inner class RecipeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
//        val titleText: TextView = view.findViewById(R.id.recipeTitle)
//        val chefText: TextView = view.findViewById(R.id.chefName)
//        val durationText: TextView = view.findViewById(R.id.duration)
//        val ratingText: TextView = view.findViewById(R.id.ratingText)
//        val recipeImage: ImageView = view.findViewById(R.id.recipeImage)
//
//        init {
//            view.setOnClickListener {
//                val recipeDetailsFragment = RecipeDetailsFragment()
//                requireActivity().supportFragmentManager.beginTransaction()
//                    .replace(R.id.frameLayout, recipeDetailsFragment)
//                    .addToBackStack(null)
//                    .commit()
//            }
//        }
//    }
//
//    private inner class RecipeAdapter : RecyclerView.Adapter<RecipeViewHolder>() {
//        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
//            val view = LayoutInflater.from(parent.context)
//                .inflate(R.layout.item_recipe_card, parent, false)
//            return RecipeViewHolder(view)
//        }
//
//        override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
//            val recipe = filteredRecipes[position]
//            holder.apply {
//                titleText.text = recipe.title
//                chefText.text = getString(R.string.chef_name_format, recipe.chef)
//                durationText.text = recipe.duration
//                ratingText.text = recipe.rating
//                recipeImage.setImageResource(recipe.image)
//            }
//        }
//
//        override fun getItemCount() = filteredRecipes.size
//    }
//
//    private fun performSearch(query: String) {
//        val oldSize = filteredRecipes.size
//        filteredRecipes.clear()
//
//        if (query.isBlank()) {
//            filteredRecipes.addAll(allRecipes)
//            if (oldSize > 0) {
//                adapter.notifyItemRangeRemoved(0, oldSize)
//            }
//            adapter.notifyItemRangeInserted(0, allRecipes.size)
//        } else { // search by query
//            val newRecipes = allRecipes.filter { recipe ->
//                recipe.title.contains(query, ignoreCase = true) ||
//                        recipe.chef.contains(query, ignoreCase = true)
//            }
//            filteredRecipes.addAll(newRecipes)
//            if (oldSize > 0) {
//                adapter.notifyItemRangeRemoved(0, oldSize)
//            }
//            if (newRecipes.isNotEmpty()) {
//                adapter.notifyItemRangeInserted(0, newRecipes.size)
//            }
//        }
//    }
//
//    private data class Recipe(
//        val title: String,
//        val chef: String,
//        val duration: String,
//        val rating: String,
//        val image: Int,
//        val category: String
//    ) {
//        fun durationInMinutes(): Int { // not working yet
//            val durationParts = duration.split(" ")
//            val durationValue = durationParts.first().toInt()
//            val durationUnit = durationParts.last()
//            return when (durationUnit) {
//                "min" -> durationValue
//                "hr" -> durationValue * 60
//                else -> 0
//            }
//        }
//    }
//
//    companion object {
//        fun newInstance() = SearchFragment()
//    }
//}