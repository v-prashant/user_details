package com.example.trendingapp.ui.user_details

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.trendingapp.R
import com.example.trendingapp.base.BaseActivity
import com.example.trendingapp.databinding.ActivityUserBinding
import com.example.trendingapp.databinding.ActivityUserDetailsBinding
import com.example.trendingapp.ui.user.Address
import com.example.trendingapp.ui.user.UserItems
import com.example.trendingapp.ui.user.UserVM
import com.example.trendingapp.utils.common.AppFunctions
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class UserDetailsActivity : BaseActivity<UserVM, ActivityUserDetailsBinding>() {

    private var isDateSelected = false
    private var bitmap: Bitmap? = null
    lateinit var takePictureLauncher: ActivityResultLauncher<Intent>

    private lateinit var adapterSy: ArrayAdapter<String>
    private lateinit var adapterEy: ArrayAdapter<String>

    private val sy: List<String> by lazy {
        listOf("2001", "2002", "2003", "2004")
    }
    private val ey: List<String> by lazy {
        listOf("2005", "2006", "2007", "2008")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initData()
        getIntentData()
        initViews()
        onClickListener()
        initPhotoUpload()
    }

    private fun onClickListener() {
         binding.btnSubmit.setOnClickListener {
             if(isValidate()){
                 setData()
                 finish()
             }
         }
        binding.etDob.setOnClickListener {
            showDate()
        }
        binding.tvUploadPhoto.setOnClickListener {
            uploadPhoto()
        }
    }

    private fun setData() {
        val intent = Intent()

        val address = Address(binding.etHouseNumber.text.toString(), binding.etArea.text.toString(), binding.etTownCity.text.toString(), binding.etState.text.toString(),
                                binding.etPincode.text.toString())

        val arrayBytes = AppFunctions.getBytesFromBitmap(bitmap)
        val userData = UserItems(arrayBytes, binding.etFirstName.text.toString(), binding.etLastName.text.toString(), binding.etMobileNumber.text.toString(), address,
            binding.spStartYear.text.toString(), binding.spEndYear.text.toString(), binding.etInstituteName.text.toString())

        intent.putExtra("position", this.intent.getIntExtra("position", -1))
        intent.putExtra("user_data", userData)

        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    private fun initPhotoUpload() {
        takePictureLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    bitmap = result.data?.extras?.get("data") as Bitmap
                    binding.ivPhotoUpload.setImageBitmap(bitmap)
                }
            }
    }

    private fun uploadPhoto() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                takePictureLauncher.launch(takePictureIntent)
            }
        }
    }

    private fun showDate() {

        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, yearSelected, monthSelected, dayOfMonthSelected ->
                val dateSelected = "${dayOfMonthSelected}/${monthSelected + 1}/${yearSelected}"
                binding.etDob.setText(dateSelected)
                isDateSelected = true
            },
            year,
            month,
            dayOfMonth
        )
        datePickerDialog.show()

    }

    private fun isValidate(): Boolean {
        with(binding){
            if(etFirstName.text.isNullOrEmpty()){
                tilFirstName.error = "Invalid First Name"
                return false
            }else if(etLastName.text.isNullOrEmpty()){
                tilLastName.error = "Invalid Last Name"
                return false
            }else if(!isDateSelected){
                tilDob.error = "Invalid Date"
                return false
            }else if(etMobileNumber.text.isNullOrEmpty() || etMobileNumber.text.toString().length < 10){
                tilMobileNumber.error = "Invalid Number"
                return false
            }else if(etHouseNumber.text.isNullOrEmpty()){
                tilHouseNumber.error = "Invalid House Address"
                return false
            }else if(etArea.text.isNullOrEmpty()){
                tilArea.error = "Invalid Area Address"
                return false
            }else if(etPincode.text.isNullOrEmpty() || etPincode.text.toString().length < 6){
                tilPincode.error = "Invalid PinCode"
                return false
            }else if(etTownCity.text.isNullOrEmpty()){
                tilTownCity.error = "Invalid Town Address"
                return false
            }else if(etState.text.isNullOrEmpty()){
                tilState.error = "Invalid State"
                return false
            }else if(etInstituteName.text.isNullOrEmpty()){
                tilInstituteName.error = "Invalid Institute"
                return false
            }
        }
        return true
    }

    private fun initData() {
        val level = listOf("Level 1", "Level 2", "Level 3", "Level 4")
        val stream = listOf("Electronic", "Computer Science", "Civil")

        val adapterLevel = ArrayAdapter(this, R.layout.list_item, level)
        val adapterStream = ArrayAdapter(this, R.layout.list_item, stream)
        adapterSy = ArrayAdapter(this, R.layout.list_item, sy)
        adapterEy = ArrayAdapter(this, R.layout.list_item, ey)

        (binding.spLevel as? AutoCompleteTextView)?.setAdapter(adapterLevel)
        (binding.spStream as? AutoCompleteTextView)?.setAdapter(adapterStream)
        (binding.spStartYear as? AutoCompleteTextView)?.setAdapter(adapterSy)
        (binding.spEndYear as? AutoCompleteTextView)?.setAdapter(adapterEy)

        with(binding){
            spLevel.setText(adapterLevel.getItem(0), false)
            spStream.setText(adapterStream.getItem(0), false)
            spStartYear.setText(adapterSy.getItem(0), false)
            spEndYear.setText(adapterEy.getItem(0), false)
        }

    }

    private fun getIntentData() {
        val list: UserItems? = intent.getSerializableExtra("user_data") as UserItems?
        with(binding){
            if(bitmap != null)
                ivPhotoUpload.setImageBitmap(bitmap)
            etFirstName.setText(list?.firstName)
            etLastName.setText(list?.lastName)
            etMobileNumber.setText(list?.mobileNumber)
            etHouseNumber.setText(list?.address?.houseNumber)
            etArea.setText(list?.address?.areaName)
            etPincode.setText(list?.address?.pinCode)
            etTownCity.setText(list?.address?.townCity)
            etState.setText(list?.address?.state)
            etInstituteName.setText(list?.instituteName)

            val startDatePosition = sy.indexOf(list?.educationStartDate)
            val endDatePosition = ey.indexOf(list?.educationEndDate)
            spStartYear.setText(adapterSy.getItem(startDatePosition), false)
            spEndYear.setText(adapterEy.getItem(endDatePosition), false)
        }
    }

    private fun initViews() {

    }

    override val layoutId: Int
        get() = R.layout.activity_user_details
}
