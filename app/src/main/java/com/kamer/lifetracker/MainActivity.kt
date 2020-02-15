package com.kamer.lifetracker

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        testView.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermission()
            } else {
                requestData()
            }
        }
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_READ_CONTACTS -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    requestData()
                } else {
                    Toast.makeText(this, "No permission", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }

    private fun requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.GET_ACCOUNTS)) {
            Toast.makeText(this, "Give permission please", Toast.LENGTH_SHORT).show()
        }
        ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.GET_ACCOUNTS),
                MY_PERMISSIONS_REQUEST_READ_CONTACTS
        )
    }

    private fun requestData() {

    }

    companion object {
        private const val MY_PERMISSIONS_REQUEST_READ_CONTACTS = 100
    }
}
