package edu.uw.ischool.jtay25.digitalrecipebook

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SearchFragment : Fragment() {
    private lateinit var searchView: SearchView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RecipeAdapter


    // sample data before we implement datalink layer
    private val allRecipes = listOf(
        Recipe("Traditional Spare Ribs", "Chef John", "20 min", "4.0", R.drawable.ribs, "Dinner"),
        Recipe("Spice Roasted Chicken", "Mark Kelvin", "20 min", "4.0", R.drawable.chicken, "Dinner"),
        Recipe("Spicy Fried Rice With Chicken Bali", "Spicy Nelly", "20 min", "4.0", R.drawable.fried_rice, "Dinner"),
        Recipe("Lamb Chops", "Chef Maria", "20 min", "3.0", R.drawable.lamb_chops,"Dinner")
    )


    private var filteredRecipes = mutableListOf<Recipe>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.search_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchView = view.findViewById(R.id.searchView)
        recyclerView = view.findViewById(R.id.searchResultsRecyclerView)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = RecipeAdapter()
        recyclerView.adapter = adapter

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
            performSearch("")
        }

        searchView.setOnCloseListener {
            filteredRecipes.clear()
            adapter.notifyDataSetChanged()
            true
        }

        // start collapsed
        searchView.isIconified = true
    }

    private inner class RecipeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleText: TextView = view.findViewById(R.id.recipeTitle)
        val chefText: TextView = view.findViewById(R.id.chefName)
        val durationText: TextView = view.findViewById(R.id.duration)
        val ratingText: TextView = view.findViewById(R.id.ratingText)
        val recipeImage: ImageView = view.findViewById(R.id.recipeImage)

        init {
            view.setOnClickListener {
                val recipeDetailsFragment = RecipeDetailsFragment()
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.frameLayout, recipeDetailsFragment)
                    .addToBackStack(null)
                    .commit()
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
                ratingText.text = recipe.rating
                recipeImage.setImageResource(recipe.image)
            }
        }

        override fun getItemCount() = filteredRecipes.size
    }

    private fun performSearch(query: String) {
        val oldSize = filteredRecipes.size
        filteredRecipes.clear()

        if (query.isBlank()) {
            filteredRecipes.addAll(allRecipes)
            if (oldSize > 0) {
                adapter.notifyItemRangeRemoved(0, oldSize)
            }
            adapter.notifyItemRangeInserted(0, allRecipes.size)
        } else { // search by query
            val newRecipes = allRecipes.filter { recipe ->
                recipe.title.contains(query, ignoreCase = true) ||
                        recipe.chef.contains(query, ignoreCase = true)
            }
            filteredRecipes.addAll(newRecipes)
            if (oldSize > 0) {
                adapter.notifyItemRangeRemoved(0, oldSize)
            }
            if (newRecipes.isNotEmpty()) {
                adapter.notifyItemRangeInserted(0, newRecipes.size)
            }
        }
    }

    private data class Recipe(
        val title: String,
        val chef: String,
        val duration: String,
        val rating: String,
        val image: Int,
        val category: String
    )

    companion object {
        fun newInstance() = SearchFragment()
    }
}