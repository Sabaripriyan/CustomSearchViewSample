package com.example.customsearchviewsample.custom_views

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.appcompat.widget.SearchView
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import io.reactivex.functions.Consumer
import io.reactivex.functions.Predicate
import io.reactivex.schedulers.Schedulers.io
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeUnit.*

class CustomSearchView(context: Context, attributeSet: AttributeSet): SearchView(context,attributeSet) {

    lateinit var subject: PublishSubject<String>
     var onFinalQueryTextListener: OnFinalQueryTextListener? = null

    init {
        init()
    }

     interface OnFinalQueryTextListener{

        fun onFinalQueryTextChange(newText: String): Boolean
        fun onQueryCleared()
    }




    private fun init(){
        subject = PublishSubject.create<String>()
        setOnQueryTextListener(object : OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                subject.onNext(query!!)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                subject.onNext(newText!!)
                onFinalQueryTextListener?.onQueryCleared()
                return true
            }

        })

        subject.debounce(300, MILLISECONDS)
            .filter(object : Predicate<String> {
                override fun test(t: String): Boolean {
                    if(t.length == 0){
                        return false
                    }else{
                        return true
                    }
                }

            })
            .distinctUntilChanged()
            .subscribeOn(io())
            .observeOn(mainThread())
            .subscribe(object : Consumer<String> {
                override fun accept(t: String?) {
                    Log.e("CustomSearchView",t)
                    onFinalQueryTextListener?.onFinalQueryTextChange(t!!)
                }
            })

    }
}