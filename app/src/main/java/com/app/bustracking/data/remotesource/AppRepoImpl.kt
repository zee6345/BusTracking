package com.app.bustracking.data.remotesource

import com.app.bustracking.data.api.ApiService
import javax.inject.Inject

class AppRepoImpl @Inject constructor(private val apiService: ApiService) : AppRepo {

}