package com.example.customsearchviewsample

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.customsearchviewsample.custom_views.CustomSearchView
import com.example.customsearchviewsample.model.Country
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_edit_dialog.*
import java.io.IOException

class MainActivity : AppCompatActivity(),OnEditClickedListener{

    override fun onEditClicked(id: Int, text: String) {
        searchView.onActionViewCollapsed()
        val iterator = countryList.listIterator()
        var position = 0
        while(iterator.hasNext()){
            position = iterator.nextIndex()
            val country = iterator.next()
            if(country.id == id){
                country.name = text
                break
            }
        }
        countryListAdapter.updateList(countryList)
        rvCountryList.smoothScrollToPosition(position)
    }

    lateinit var countryList: ArrayList<Country>
    lateinit var countryListAdapter: CountryListAdapter
    lateinit var layoutManager: RecyclerView.LayoutManager
    var id = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
        setupDefaults()
        setupEvents()
    }

    private fun init(){
        supportActionBar!!.title = getString(R.string.str_countries)
        countryList = ArrayList()
        countryListAdapter = CountryListAdapter(this,countryList,this)
        layoutManager = LinearLayoutManager(this,RecyclerView.VERTICAL,false)
    }

    private fun setupDefaults() {
        rvCountryList.layoutManager = this.layoutManager
        rvCountryList.adapter = this.countryListAdapter

        val string = getJSONFromAssets("country_list.json")
        val type = object: TypeToken<ArrayList<Country>>(){}.type
        countryList = Gson().fromJson(string,type)
        countryListAdapter.updateList(countryList)
    }

    private fun setupEvents() {
        searchView.onFinalQueryTextListener = object : CustomSearchView.OnFinalQueryTextListener{
            override fun onQueryCleared() {
                countryListAdapter.getFilter().filter("")
            }

            override fun onFinalQueryTextChange(newText: String): Boolean {
                countryListAdapter.getFilter().filter(newText)
                return true
            }
        }

        fab.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                showAddDialog()
            }
        })
    }

    private fun getJSONFromAssets(fileName: String): String?{
        val jsonString: String
        try{
            val inputStream = assets.open(fileName)
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.use {
                it.read(buffer)
            }
            jsonString = String(buffer)

        }catch (exception: IOException){
            exception.printStackTrace()
            return null
        }
        return jsonString
    }

    fun showAddDialog(){
         val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.layout_edit_dialog)
        dialog.setCancelable(false)
        val lp =  WindowManager.LayoutParams()
        lp.copyFrom(dialog.getWindow()?.getAttributes())
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog.window?.attributes = lp
        dialog.show()
        dialog.btnAddEdit.text = this.getString(R.string.str_add)
        dialog.btnAddEdit.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                searchView.onActionViewCollapsed()
                id++
                countryList.add(Country("",dialog.editText.text.toString(),true,id))
                countryListAdapter.updateList(countryList)
                rvCountryList.smoothScrollToPosition(countryList.size-1)
                dialog.dismiss()
            }
        })
        dialog.imageClose.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                dialog.dismiss()
            }
        })
    }

}
