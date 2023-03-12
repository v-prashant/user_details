package com.example.trendingapp.ui.user

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.trendingapp.R
import com.example.trendingapp.databinding.ItemUserBinding
import com.example.trendingapp.ui.user_details.UserDetailsActivity
import com.example.trendingapp.utils.common.AppFunctions

class UserAdapter(
    var context: Context,
    private var dataList: ArrayList<UserItems?>,
    private val userDetailsResultLauncher: ActivityResultLauncher<Intent>
) : RecyclerView.Adapter<UserAdapterViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): UserAdapterViewHolder {
        val binding = DataBindingUtil.inflate<ItemUserBinding>(
            LayoutInflater.from(context), R.layout.item_user,
            parent, false
        )
        return UserAdapterViewHolder(binding)
    }
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
    override fun getItemViewType(position: Int): Int {
        return position
    }
    override fun getItemCount(): Int {
         return dataList.size
    }
    override fun onBindViewHolder(holder: UserAdapterViewHolder, position: Int) {
         with(holder.binding){
             tvName.text = dataList[holder.adapterPosition]?.firstName+" "+dataList[holder.adapterPosition]?.lastName
             tvNumber.text = dataList[holder.adapterPosition]?.mobileNumber
             tvAddress.text = dataList[holder.adapterPosition]?.address?.houseNumber+" "+dataList[holder.adapterPosition]?.address?.areaName+" "+dataList[holder.adapterPosition]?.address?.townCity+" "+
                                dataList[holder.adapterPosition]?.address?.state+" "+dataList[holder.adapterPosition]?.address?.pinCode
             tvGraduationTime.text = dataList[holder.adapterPosition]?.educationStartDate+"-"+dataList[holder.adapterPosition]?.educationEndDate
             tvInstituteName.text = dataList[holder.adapterPosition]?.instituteName

             if(dataList[holder.adapterPosition]?.photo != null){
                 val bitmap = AppFunctions.getBitmap(dataList[holder.adapterPosition]?.photo)
                 ivPhoto.setImageBitmap(bitmap)
             }
         }

        holder.binding.ivEdit.setOnClickListener {
            val intent = Intent(context, UserDetailsActivity::class.java)
            intent.putExtra("user_data", dataList[holder.layoutPosition])
            intent.putExtra("position", holder.layoutPosition)
            userDetailsResultLauncher.launch(intent)
        }
    }

    fun updateDataList(list: ArrayList<UserItems?>){
        this.dataList = list
    }

}

class UserAdapterViewHolder(var binding: ItemUserBinding) :
    RecyclerView.ViewHolder(binding.root)

data class UserItems(val photo: ByteArray?, val firstName: String?, val lastName: String?, val mobileNumber: String?, val address: Address?, val educationStartDate: String?,
                     val educationEndDate: String?, val instituteName: String?): java.io.Serializable

data class Address(val houseNumber: String?, val areaName: String?, val townCity: String?, val state: String?, val pinCode: String?): java.io.Serializable



