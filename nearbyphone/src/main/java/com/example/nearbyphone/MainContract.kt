package com.example.kennyrizky.nearbyprint

import android.content.Context

/**
 * Created by jurnal on 27/06/18.
 */
interface MainContract {

    interface View {

    }

    interface Presenter {

        fun startDiscovery(context: Context)

        fun stopDiscovery()

        fun detachView()

        fun attachView(view: View)

    }

}