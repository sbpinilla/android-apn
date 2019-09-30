package com.sbpinilla.apn

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telephony.TelephonyManager
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_APN_SETTINGS) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED  &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED ) {

            ActivityCompat.requestPermissions(this,
                    arrayOf(
                            Manifest.permission.WRITE_APN_SETTINGS,
                            Manifest.permission.READ_PHONE_NUMBERS,
                            Manifest.permission.READ_PHONE_STATE), 12);

            // MY_PERMISSIONS_REQUEST_CAMERA is an
            // app-defined int constant. The callback method gets the
            // result of the request.

        } else {
            // Permission has already been granted
            loadData()
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        if(requestCode == 12){
            loadData()
        }

    }

    private fun loadData() {

        val tm = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

        val operator = tm.simOperator
        val operator1 = tm.simOperatorName
        val operator2 = tm.line1Number


        Log.d("*****","$operator")
        Log.d("*****","$operator1")
        Log.d("*****","$operator2")







    }

    fun dataSend(view: View) {

        val intent = Intent(this, NewAPNActivity::class.java)

        startActivity(intent)
    }


}
