package com.example.trendingapp.ui.user

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.trendingapp.R
import com.example.trendingapp.base.BaseActivity
import com.example.trendingapp.databinding.ActivityUserBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserActivity : BaseActivity<UserVM, ActivityUserBinding>() {

    private lateinit var dataList: ArrayList<UserItems?>
    private val tempList = ArrayList<UserItems?>()
    private lateinit var userDetailsResultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initData()
        onResult()
        initViews()
        initSearchBar()
    }

    private fun onResult() {
        userDetailsResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent: Intent? = result.data
                getData(intent)
            }
        }
    }

    private fun getData(intent: Intent?) {
        val userItem = intent?.getSerializableExtra("user_data") as? UserItems?
        val position = intent?.getIntExtra("position", -1)
        if(userItem != null && position != -1) {
            dataList[position!!] = userItem
            (binding.rvUser.adapter as UserAdapter).updateDataList(dataList)
            binding.rvUser.adapter?.notifyItemChanged(position)
        }
    }

    private fun initData() {
        dataList = ArrayList()
        val address = Address("11c", "HSR Layout","Bangalore", "Karnataka", "560010")
        dataList.add(UserItems(null, "Prashant", "Verma", "8173816187", address, "2002","2007",
                "Shree Jeet Ram Smarak, Bareilly, Utter Pradesh"))
        dataList.add(UserItems(null, "Shiva", "Verma", "8173816187", address, "2002","2007",
            "Shree Jeet Ram Smarak, Bareilly, Utter Pradesh"))
        dataList.add(UserItems(null, "Bikki", "Verma", "8173816187", address, "2002","2006",
            "Shree Jeet Ram Smarak, Bareilly, Utter Pradesh"))
        dataList.add(UserItems(null, "Honey", "Verma", "8173816187", address, "2001","2008",
            "Shree Jeet Ram Smarak, Bareilly, Utter Pradesh"))
        dataList.add(UserItems(null, "Tusar", "Verma", "8173816187", address, "2002","2007",
            "Shree Jeet Ram Smarak, Bareilly, Utter Pradesh"))
        dataList.add(UserItems(null, "Mani", "Shrivastav", "8173816187", address, "2002","2007",
            "Shree Jeet Ram Smarak, Bareilly, Utter Pradesh"))

    }

    private fun initSearchBar() {
        binding.searchBar.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                 searchUsers(s)
            }
            override fun afterTextChanged(s: Editable?) {}

        })
    }

    private fun searchUsers(s: CharSequence?) {
        tempList.clear()
        for(i in dataList){
            val name = i?.firstName+" "+i?.lastName
            if(s != null && name.contains(s, ignoreCase = true))
                tempList.add(i)
        }
        (binding.rvUser.adapter as UserAdapter).updateDataList(tempList)
        binding.rvUser.adapter?.notifyDataSetChanged()
    }

    private fun initViews() {
        tempList.addAll(dataList)
        binding.rvUser.adapter = UserAdapter(this, tempList, userDetailsResultLauncher)
    }

    override val layoutId: Int
        get() = R.layout.activity_user
}
