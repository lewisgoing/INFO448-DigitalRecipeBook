package edu.uw.ischool.jtay25.digitalrecipebook

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID

class AddRecipeActivity : AppCompatActivity() {

    private lateinit var editTextTitle: EditText
    private lateinit var editTextChef: EditText
    private lateinit var editTextDuration: EditText
    private lateinit var spinnerCategory: Spinner
    private lateinit var editTextIngredients: EditText
    private lateinit var editTextInstructions: EditText
    private lateinit var buttonAddRecipe: Button
    private lateinit var buttonCancel: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.add_recipe_layout) // Same layout as used in the fragment

        // Initialize UI components
        editTextTitle = findViewById(R.id.editTextTitle)
        editTextChef = findViewById(R.id.editTextChef)
        editTextDuration = findViewById(R.id.editTextDuration)
        spinnerCategory = findViewById(R.id.spinnerCategory)
        editTextIngredients = findViewById(R.id.editTextIngredients)
        editTextInstructions = findViewById(R.id.editTextInstructions)
        buttonAddRecipe = findViewById(R.id.buttonAddRecipe)
        buttonCancel = findViewById(R.id.buttonCancel)

        // Set the click listener for the save button
        buttonAddRecipe.setOnClickListener { saveRecipeToFirebase() }
        buttonCancel.setOnClickListener{startActivity(Intent(this,MainActivity::class.java))}
    }

    private fun saveRecipeToFirebase() {
        val title = editTextTitle.text.toString()
        val chef = editTextChef.text.toString()
        val duration = editTextDuration.text.toString()
        val category = spinnerCategory.selectedItem.toString()
        val ingredients = editTextIngredients.text.toString().split(",").map { it.trim() }
        val instructions = editTextInstructions.text.toString().split("\n").map { it.trim() }

        if (title.isEmpty() || chef.isEmpty() || duration.isEmpty() || category.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val recipe = Recipe(
            id = UUID.randomUUID().toString(), // Generate unique ID
            title = title,
            chef = chef,
            duration = duration,
            category = category,
            ingredients = ingredients,
            instructions = instructions
        )

        // Use Realtime Database
        val db = com.google.firebase.database.FirebaseDatabase.getInstance()
        val recipesRef = db.getReference("recipes")

        recipesRef.child(recipe.id).setValue(recipe)
            .addOnSuccessListener {
                Toast.makeText(this, "Recipe saved successfully!", Toast.LENGTH_SHORT).show()
                finish() // Close the activity and return to the previous screen
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to save recipe: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
