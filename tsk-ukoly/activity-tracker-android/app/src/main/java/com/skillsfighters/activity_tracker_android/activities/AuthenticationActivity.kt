package com.skillsfighters.activity_tracker_android.activities

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.google.firebase.auth.FirebaseAuth
import com.skillsfighters.activity_tracker_android.BuildConfig
import com.skillsfighters.activity_tracker_android.R
import kotlinx.android.synthetic.main.activity_authentication.*

class AuthenticationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication)

        if (FirebaseAuth.getInstance().currentUser != null) {
            signin_button.visibility = View.GONE
            val user = FirebaseAuth.getInstance().currentUser
            val userName = user?.displayName
            username_view.text = userName
        }
        else {
            continue_button.visibility = View.GONE
            username_view.visibility = View.GONE
        }
    }

    fun createSignInIntent(view: View) {
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build(),
            AuthUI.IdpConfig.FacebookBuilder().build()
        )

        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build(),
            RC_SIGN_IN
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in
                signin_button.visibility = View.GONE
                continue_button.visibility = View.VISIBLE
                username_view.visibility = View.VISIBLE
                val user = FirebaseAuth.getInstance().currentUser
                val userName = user?.displayName
                if (userName != null) {
                    username_view.text = userName
                }
            }
            else {
                if (response == null) {
                    Log.e("Login", "User canceled login process")
                }
                else {
                    Log.e("Login", response.error?.errorCode.toString())
                }
            }
        }
    }

    private fun signoutUser() {
        AuthUI.getInstance()
            .signOut(this)
            .addOnCompleteListener {
                val userName = FirebaseAuth.getInstance().currentUser?.displayName
                if (userName != null) {
                    signin_button.visibility = View.GONE
                    continue_button.visibility = View.VISIBLE
                    username_view.visibility = View.VISIBLE
                    username_view.text = userName
                }
                else {
                    signin_button.visibility = View.VISIBLE
                    continue_button.visibility = View.GONE
                    username_view.visibility = View.GONE
                    Toast.makeText(this,
                        resources.getString(R.string.user_logged_out),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

    private fun deleteUser() {
        AuthUI.getInstance()
            .delete(this)
            .addOnCompleteListener {
                val userName = FirebaseAuth.getInstance().currentUser?.displayName
                if (userName != null) {
                    signin_button.visibility = View.GONE
                    continue_button.visibility = View.VISIBLE
                    username_view.visibility = View.VISIBLE
                    username_view.text = userName
                }
                else {
                    signin_button.visibility = View.VISIBLE
                    continue_button.visibility = View.GONE
                    username_view.visibility = View.GONE
                    Toast.makeText(this,
                        getString(R.string.user_deleted),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

    private fun deleteUserDialog() {
        val dialogBuilder = AlertDialog.Builder(this)

        dialogBuilder.setTitle(getString(R.string.delete_user))
        dialogBuilder.setMessage(getString(R.string.delete_user_message))
        dialogBuilder.setPositiveButton(getString(R.string.delete)) { dialog, whichButton ->
            deleteUser()
        }
        dialogBuilder.setNegativeButton(getString(R.string.cancel)) { dialog, whichButton ->
            return@setNegativeButton
        }
        val b = dialogBuilder.create()
        b.show()
    }

    private fun appInfo() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle(getString(R.string.app_name))
        alertDialogBuilder.setMessage(getString(R.string.version) + " ${BuildConfig.VERSION_NAME}/${BuildConfig.VERSION_CODE}")

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun licenseInfo() {
        val intent = Intent(this, OssLicensesMenuActivity::class.java)
        startActivity(intent)
    }

    fun continueToApp(view: View) {
        val intent = Intent(this, GroupActivity::class.java)
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.auth_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_signout -> {
            signoutUser()
            true
        }
        R.id.action_delete -> {
            deleteUserDialog()
            true
        }
        R.id.app_info -> {
            appInfo()
            true
        }
        R.id.license_info -> {
            licenseInfo()
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    companion object {
        // Request code used in onActivityResult
        // by the activity to identify itself
        private const val RC_SIGN_IN = 123
    }
}
