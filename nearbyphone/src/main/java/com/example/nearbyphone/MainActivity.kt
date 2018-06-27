package com.example.nearbyphone

import android.Manifest
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.view.View
import android.widget.Button
import com.example.kennyrizky.nearbyprint.MainContract
import com.example.kennyrizky.nearbyprint.MainPresenter

class MainActivity : AppCompatActivity(), MainContract.View {

    private var mainPresenter: MainContract.Presenter? = null
    private val REQUEST_CODE_LOCATION_PERMISSION = 569

    private lateinit var btnStart : Button
    private lateinit var btnStop : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mainPresenter = MainPresenter().also {
                it.attachView(this)
            }
        }
        else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), REQUEST_CODE_LOCATION_PERMISSION)
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        mainPresenter?.detachView()
    }

    /**
     * On Request Permission Result
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == REQUEST_CODE_LOCATION_PERMISSION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            mainPresenter = MainPresenter().also {
                it.attachView(this)
            }
        }
    }

    /**
     * View Method
     */
    fun btnStartDidClick(view: View) {
        mainPresenter?.startDiscovery(this)
    }

    fun btnStopDidClick(view: View) {
        mainPresenter?.stopDiscovery()
    }
}
