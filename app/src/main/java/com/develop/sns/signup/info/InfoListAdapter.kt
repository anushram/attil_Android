package com.develop.sns.signup.info

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.develop.sns.R
import com.develop.sns.databinding.InfoListItemTmplBinding
import com.develop.sns.listener.AppUserListener
import com.squareup.picasso.Picasso
import com.talentmicro.icanrefer.dto.ModuleDto

class InfoListAdapter(
    val context: Context,
    val items: List<ModuleDto>,
    val appUserListener: AppUserListener
) :
    RecyclerView.Adapter<InfoListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = InfoListItemTmplBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(items[position], position)

    inner class ViewHolder(val binding: InfoListItemTmplBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ModuleDto, position: Int) {
            with(binding) {
                tvName.text = item.moduleTitle
                if (item.isCompleted == 1) {
                    Picasso.with(context).load(R.drawable.verified)
                        .placeholder(R.drawable.verified)
                        .error(R.drawable.verified)
                        .into(ivPicture);
                    if (position == 0) {
                        if (!item.mobileNo!!.isEmpty()) {
                            tvNumber.visibility = View.VISIBLE
                            tvNumber.text = item.mobileNo
                        }
                    } else if (position == 1) {
                        if (!item.userId!!.isEmpty()) {
                            tvNumber.visibility = View.VISIBLE
                            tvNumber.text = item.userId
                        }
                    }
                } else {
                    Picasso.with(context).load(R.drawable.close)
                        .placeholder(R.drawable.close)
                        .error(R.drawable.close)
                        .into(ivPicture);
                }
                lnMain.setOnClickListener {
                    appUserListener.selectItem(position)
                }

            }
        }

    }
}