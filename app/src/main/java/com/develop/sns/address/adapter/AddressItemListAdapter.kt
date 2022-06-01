package com.develop.sns.cart.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.develop.sns.R
import com.develop.sns.address.dto.AddressListDto
import com.develop.sns.address.listener.AddressListener
import com.develop.sns.databinding.AddressListItemTmplBinding
import com.develop.sns.utils.PreferenceHelper


class AddressItemListAdapter(
    val context: Context,
    val items: ArrayList<AddressListDto>,
    val addressListener: AddressListener,
) : RecyclerView.Adapter<AddressItemListAdapter.ViewHolder>() {

    var preferenceHelper = PreferenceHelper(context)
    private var lastCheckedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = AddressListItemTmplBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(items.get(position), position, items)

    inner class ViewHolder(val binding: AddressListItemTmplBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(addressListDto: AddressListDto, position: Int, items: ArrayList<AddressListDto>) {
            with(binding) {

                rbSelect.isChecked = position == lastCheckedPosition;

                tvAddress.text =
                    addressListDto.doorNo.plus(", ")
                        .plus(addressListDto.street).plus(", ")
                        .plus(addressListDto.townORcity).plus(", ")
                        .plus(addressListDto.pinCode)

                tvLandmark.text = context.getString(R.string.landmark_title).plus(" ")
                    .plus(addressListDto.landmark)

                tvPhoneNumber.text = addressListDto.phoneNumber

                rbSelect.setOnClickListener {
                    val copyOfLastCheckedPosition = lastCheckedPosition
                    lastCheckedPosition = adapterPosition
                    notifyItemChanged(copyOfLastCheckedPosition)
                    notifyItemChanged(lastCheckedPosition)
                    val addressDto = items[lastCheckedPosition]
                    addressListener.selectItem(addressDto)
                }

                tvChange.setOnClickListener {
                    addressListener.edit(addressListDto)
                }
            }
        }

    }
}