package com.example.trendingapp.ui.user

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.trendingapp.api.Resource
import com.example.trendingapp.base.BaseViewModel
import com.example.trendingapp.network.response.GetRepositoriesResponse
import com.example.trendingapp.utils.common.setError
import com.example.trendingapp.utils.common.setLoading
import com.example.trendingapp.utils.common.setSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class UserVM @Inject constructor(val repository: UserRepository) :
    BaseViewModel() {

}