package com.example.trendingapp.base

import com.example.trendingapp.api.Resource
import com.example.trendingapp.api.Status
import com.example.trendingapp.utils.ProgressUtil
import com.example.trendingapp.utils.ToastUtils
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.MutableLiveData
import com.example.trendingapp.network.BaseResponse
import com.google.gson.Gson
import javax.inject.Inject

/**
 * Created by Prashant Verma
 */
abstract class BaseActivity<V : BaseViewModel, B : ViewDataBinding> : AppCompatActivity(),
    View.OnClickListener {

    @Inject
    lateinit var viewModel: V

    @Inject
    lateinit var gson: Gson

    lateinit var binding: B
        private set

    @get:LayoutRes
    abstract val layoutId: Int

    private lateinit var progressDialog: ProgressUtil

    var instanceIdByLoanAppId: String? = null

    var loanAppId: String = ""

    override fun onClick(p0: View?) {
        // override in activity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, layoutId)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        initProgress()
    }

    private fun initProgress() {
        progressDialog = ProgressUtil(this)
    }

    open fun showProgress() {
        progressDialog.showDialog("")
    }

    open fun showProgress(progressMessage: String?) {
        if (!progressDialog.isShowing) {
            progressDialog.showDialog(progressMessage ?: "")
        }
    }

    open fun hideProgress() {
        if (progressDialog.isShowing) {
            progressDialog.dismissDialog()
        }
    }

    fun showErrorMessage(message: String) {
        showToastMessage(message, ToastUtils.ToastType.ERROR, ToastUtils.HeaderToastType.ERROR)
    }

    fun showErrorMessage(response: BaseResponse.ResponseStatus) {
        showErrorMessage(response.message)
    }

    fun showSuccessMessage(message: String) {
        ToastUtils.makeToast(
            ToastUtils.ToastType.SUCCESS,
            ToastUtils.HeaderToastType.SUCCESS, message)
    }

    fun showToastMessage(message: String, type: ToastUtils.ToastType, headerToastType: ToastUtils.HeaderToastType) {
        ToastUtils.makeToast(type,headerToastType, message, Toast.LENGTH_SHORT)
    }

    fun <T> MutableLiveData<Resource<T>>.observeLiveData(
        success: (t: T) -> Unit, error: ((e: BaseResponse.ResponseStatus) -> Unit)? = null,
    ) {
        observe(this@BaseActivity) {
            it?.let {
                when (it.status) {
                    Status.LOADING -> {
                        showProgress(it.message)
                    }
                    Status.SUCCESS -> {
                        it.data?.let { response ->
                            success.invoke(response)
                        }
                    }
                    Status.ERROR -> {
                        hideProgress()
                        (it.data as BaseResponse).responseStatus?.let { it ->
                            if (it.code == "11016" || it.code == "11018") {
                                showErrorMessage(it.message)
                       //         val i = Intent(this@BaseActivity, LoginActivity::class.java)
                       //         i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                       //         startActivity(i)
                            } else if (error == null) {
                                showErrorMessage(it.message)
                            }
                            error?.invoke(it)
                        }
                    }
                    Status.THROWABLE -> {
                        hideProgress()
                        val errorBody = it.throwable
                        showErrorMessage(errorBody?.message.toString())
                    }
                }
            }
        }
    }
}