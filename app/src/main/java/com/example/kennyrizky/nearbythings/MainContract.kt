package com.example.kennyrizky.nearbythings

/**
 * Created by jurnal on 27/06/18.
 */
interface MainContract {

    interface View {

        fun showToast(text: String)

    }

    interface Presenter {

        fun detachView()

        fun attachView(view: View)

    }

}