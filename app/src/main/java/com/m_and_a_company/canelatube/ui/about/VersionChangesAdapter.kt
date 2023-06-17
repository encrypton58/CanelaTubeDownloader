package com.m_and_a_company.canelatube.ui.about

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.m_and_a_company.canelatube.databinding.ItemRegisterChangeBinding
import com.m_and_a_company.canelatube.domain.data.models.ChangeVersionModel

class VersionChangesAdapter(
    private val dataList: List<ChangeVersionModel>
) : RecyclerView.Adapter<VersionChangesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemRegisterChangeBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = dataList[position]
        holder.bind(data)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class ViewHolder(private val binding: ItemRegisterChangeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: ChangeVersionModel) {
            binding.apply {
                tvItemRegisterChangeDate.text = data.date
                tvItemRegisterChangeVersion.text = data.version
                tvItemRegisterChangeChange.text = data.change
                tvItemRegisterChangeFixed.text = data.fixed
            }
        }
    }
}

