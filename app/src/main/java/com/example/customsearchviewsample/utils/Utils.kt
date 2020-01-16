package com.example.customsearchviewsample.utils

import android.content.Context
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.example.customsearchviewsample.R
import com.pixplicity.sharp.Sharp
import okhttp3.*
import java.io.IOException

class Utils {

    companion object{
        var httpClient: OkHttpClient? = null

        fun fetchSVG(context: Context, url: String, target: ImageView){
            if(httpClient == null){
                httpClient = OkHttpClient.Builder()
                    .cache(Cache(context.cacheDir,5*1024*1014))
                    .build()

            }

            var request = Request.Builder().url(url).build()
            httpClient?.newCall(request)?.enqueue(object : Callback{
                override fun onFailure(call: Call, e: IOException) {
                    target.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.place_holder))
                }

                override fun onResponse(call: Call, response: Response) {
                    var inputStream = response.body()!!.byteStream()
                    Sharp.loadInputStream(inputStream).into(target)
                    inputStream.close()
                }

            })
        }
    }
}