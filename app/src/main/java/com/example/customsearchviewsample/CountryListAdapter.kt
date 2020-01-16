package com.example.customsearchviewsample

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.PictureDrawable
import android.view.*
import android.view.Window.*
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.customsearchviewsample.model.Country
import com.example.customsearchviewsample.utils.Utils
import kotlinx.android.synthetic.main.country_recycler_item.view.*
import kotlinx.android.synthetic.main.country_recycler_item.view.textCountryName
import kotlinx.android.synthetic.main.country_recycler_item_with_button.view.*
import kotlinx.android.synthetic.main.country_recycler_item_with_image.view.*
import kotlinx.android.synthetic.main.layout_edit_dialog.*

class CountryListAdapter(var mContext: Context, countryList: ArrayList<Country>,var onEditClickedListener: OnEditClickedListener): RecyclerView.Adapter<RecyclerView.ViewHolder>(),Filterable {
    var countryList = ArrayList(countryList)
    var countryListFiltered = ArrayList(countryList)
    val VIEW_TITLE = 0
    val VIEW_WITH_BUTTTON = 1
    val VIEW_WITH_IMAGE = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        var itemView: View? = null
        if(viewType == 0)
            itemView = LayoutInflater.from(parent.context).inflate(R.layout.country_recycler_item,parent,false)
        else if(viewType == 1)
            itemView = LayoutInflater.from(parent.context).inflate(R.layout.country_recycler_item_with_button,parent,false)
        else if(viewType == 2)
            itemView = LayoutInflater.from(parent.context).inflate(R.layout.country_recycler_item_with_image,parent,false)
        return CountryViewHolder(itemView!!,viewType,countryListFiltered,this)
    }

    override fun getItemCount(): Int {
        return countryListFiltered.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder is CountryViewHolder){
            holder.bindView(countryListFiltered[position],mContext,onEditClickedListener)
        }
    }

    override fun getItemViewType(position: Int): Int {

        if(countryListFiltered[position].flag.isEmpty() && !countryListFiltered[position].isAddedByUser){
            return VIEW_TITLE
        }else if(countryListFiltered[position].isAddedByUser){
            return VIEW_WITH_BUTTTON
        }else if(countryListFiltered[position].flag.isNotEmpty()){
            return VIEW_WITH_IMAGE
        }
        return -1
    }

    fun updateList(countryList: ArrayList<Country>){
        this.countryListFiltered = ArrayList(countryList)
        this.countryList = ArrayList(countryList)
        notifyDataSetChanged()
    }

    fun addList(countryList: ArrayList<Country>){
        this.countryListFiltered.addAll(countryList)
        notifyDataSetChanged()
    }

    override fun getFilter(): Filter {
        return object : Filter(){
            override fun performFiltering(p0: CharSequence?): FilterResults {
                var filteredList = ArrayList<Country>()
                if(p0.isNullOrEmpty()){
                    countryListFiltered = countryList
                }else{
                    for(country in countryList){
                        if(country.name.contains(p0!!,true)){
                            filteredList.add(country)
                        }
                    }
                    countryListFiltered = filteredList
                }

                val filterResults = FilterResults()
                filterResults.values = countryListFiltered

                return filterResults
            }

            override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
                countryListFiltered = (p1!!.values) as ArrayList<Country>
                notifyDataSetChanged()
            }
        }
    }

    class CountryViewHolder(var view: View, var viewType: Int, var countyList: ArrayList<Country>,var adapter: CountryListAdapter ) : RecyclerView.ViewHolder(view){

         fun bindView(country: Country,mContext: Context,onEditClickedListener: OnEditClickedListener){
            if(this.viewType == 0){
                view.textCountryName.text = country.name
            }else if(this.viewType == 1){
                view.textCountryName.text = country.name
                view.btnEdit.setOnClickListener {
                    showAddDialog(mContext,country.id,onEditClickedListener)
                }
            }else if(this.viewType == 2){
                view.textCountryName.text = country.name
                Utils.fetchSVG(mContext,country.flag,view.imageFlag)
            }

        }

        fun showAddDialog(context: Context,id: Int,onEditClickedListener: OnEditClickedListener){
            val dialog = Dialog(context)
            dialog.requestWindowFeature(FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.layout_edit_dialog)
            dialog.setCancelable(false)
            val lp =  WindowManager.LayoutParams()
            lp.copyFrom(dialog.getWindow()?.getAttributes())
            lp.width = WindowManager.LayoutParams.MATCH_PARENT
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT
            dialog.window?.attributes = lp
            dialog.show()
            dialog.btnAddEdit.text = context.getString(R.string.str_edit)
            dialog.btnAddEdit.setOnClickListener(object : View.OnClickListener{
                override fun onClick(p0: View?) {
                    val text = dialog.editText.text.toString()
                    onEditClickedListener.onEditClicked(id,text)
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
}