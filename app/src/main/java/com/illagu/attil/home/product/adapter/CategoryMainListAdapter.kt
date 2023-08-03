package com.illagu.attil.home.product.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.illagu.attil.R
import com.illagu.attil.databinding.CategoryMainListItemTmplBinding
import com.illagu.attil.home.product.listener.CategoryMainListener
import com.illagu.attil.utils.PreferenceHelper
import com.squareup.picasso.Picasso
import com.talentmicro.icanrefer.dto.CategoryMainDto


class CategoryMainListAdapter(
    val context: Context,
    val items: ArrayList<CategoryMainDto>?,
    val topOfferListener: CategoryMainListener,
    val screenWidth: Int,
) : RecyclerView.Adapter<CategoryMainListAdapter.ViewHolder>() {

    var preferenceHelper = PreferenceHelper(context)
    var measureText = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CategoryMainListItemTmplBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = items?.size!!
    //override fun getItemCount(): Int = 5

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(items?.get(position)!!, position)

    inner class ViewHolder(val binding: CategoryMainListItemTmplBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CategoryMainDto, position: Int) {
            with(binding) {
                Picasso.with(context).load(item.categoryImage)
                    .placeholder(R.drawable.product)
                    .error(R.drawable.product).fit()
                    .into(ivProduct)
                if (item.categoryName.isNotEmpty()) {
                    tvProductName.visibility = View.VISIBLE
                    tvProductName.text = item.categoryName
                } else {
                    tvProductName.visibility = View.VISIBLE
                    tvProductName.text = "NA"
                }

                if (item.isSelected) {
                    lnMain.setBackgroundResource(R.drawable.boundary_category_selected)
                } else {
                    lnMain.setBackgroundResource(R.drawable.boundary_category)
                }

                lnMain.setOnClickListener {
                    topOfferListener.selectCategoryItem(item, position)
                }

            }
        }
    }
}