package com.royalteck.progtobi.accidentreport

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {

    lateinit var firestore: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        firestore = FirebaseFirestore.getInstance()

        submit_reportBtn.setOnClickListener { view ->
            if (locEditText.text.isEmpty() || description.text.isEmpty())
                Toast.makeText(this, "Fields Cannot be Empty", Toast.LENGTH_LONG).show()
            else
                submitReport(locEditText.text.toString(), description.text.toString())
        }

    }

    private fun submitReport(location: String, descriptionString: String) {
        // Create a new user with a first, middle, and last name
        val report = hashMapOf(
            "location" to location,
            "description" to descriptionString
        )

// Add a new document with a generated ID
        firestore.collection("reports")
            .add(report)
            .addOnSuccessListener { documentReference ->
                Log.d("SNAPSHOTID", "DocumentSnapshot added with ID: ${documentReference.id}")
                Toast.makeText(this, "Report Submitted Successfully", Toast.LENGTH_LONG).show()
                locEditText.text.clear()
                description.text.clear()
            }
            .addOnFailureListener { e ->
                Log.w("Error", "Error adding document", e)
                Toast.makeText(this, "Error: " + e, Toast.LENGTH_LONG).show()
            }


    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        val menuItem: MenuItem = menu.findItem(R.id.action_reports)
        if (FirebaseUtil.isAdmin)
            menuItem.setVisible(true)
        else
            menuItem.setVisible(false)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_reports -> {
                Toast.makeText(this, "Toast", Toast.LENGTH_LONG).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    }

    override fun onResume() {
        super.onResume()
        FirebaseUtil.openFbReference(this)
        FirebaseUtil.attachListener()
    }

    fun showMenu() {
        invalidateOptionsMenu()
    }
}
