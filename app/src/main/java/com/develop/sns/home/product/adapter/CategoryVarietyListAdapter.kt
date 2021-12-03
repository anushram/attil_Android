package com.develop.sns.home.product.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.develop.sns.R
import com.develop.sns.databinding.CategoryVarietlyListItemTmplBinding
import com.develop.sns.home.product.listener.CategoryProductListener
import com.develop.sns.utils.PreferenceHelper
import com.squareup.picasso.Picasso
import com.talentmicro.icanrefer.dto.CategoryProductDto


class CategoryVarietyListAdapter(
    val context: Context,
    val items: ArrayList<CategoryProductDto>?,
    val categoryProductListener: CategoryProductListener,
    val screenWidth: Int,
) : RecyclerView.Adapter<CategoryVarietyListAdapter.ViewHolder>() {

    var preferenceHelper = PreferenceHelper(context)
    var measureText = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CategoryVarietlyListItemTmplBinding.inflate(
            inflater,
            parent,
            false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = items?.size!!

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(items?.get(position)!!, position)

    inner class ViewHolder(val binding: CategoryVarietlyListItemTmplBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CategoryProductDto, position: Int) {
            with(binding) {

                val tileWidth = (screenWidth - 32) / 2
                cvMain.requestLayout()
                cvMain.layoutParams.width = tileWidth

                when {
                    position % 4 == 0 -> {
                        cvMain.setCardBackgroundColor(
                            ContextCompat.getColor(
                                context,
                                R.color.md_pink_50
                            )
                        )
                    }
                    position % 4 == 1 -> {
                        cvMain.setCardBackgroundColor(
                            ContextCompat.getColor(
                                context,
                                R.color.md_deep_orange_50
                            )
                        )
                    }
                    position % 4 == 2 -> {
                        cvMain.setCardBackgroundColor(
                            ContextCompat.getColor(
                                context,
                                R.color.md_lime_50
                            )
                        )
                    }
                    position % 4 == 3 -> {
                        cvMain.setCardBackgroundColor(
                            ContextCompat.getColor(
                                context,
                                R.color.md_green_50
                            )
                        )
                    }
                }

                Picasso.with(context).load(item.commonImage)
                    .placeholder(R.drawable.product)
                    .error(R.drawable.product).fit()
                    .into(ivProduct)

                if (item.productName.isNotEmpty()) {
                    tvProductName.visibility = View.VISIBLE
                    tvProductName.text = item.productName
                } else {
                    tvProductName.visibility = View.INVISIBLE
                }

                if (item.brands > 0) {
                    lnVariety.visibility = View.VISIBLE
                    tvVariety.text = item.brands.toString().plus(" ")
                        .plus(context.getString(R.string.brands))
                } else {
                    lnVariety.visibility = View.INVISIBLE
                }

                lnMain.setOnClickListener {
                    categoryProductListener.selectCategoryProductItem(item, position)
                }

            }
        }
    }
}