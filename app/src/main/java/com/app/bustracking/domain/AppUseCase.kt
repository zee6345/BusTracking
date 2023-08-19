package com.app.bustracking.domain

import com.app.bustracking.data.remotesource.AppRepoImpl
import javax.inject.Inject

class AppUseCase @Inject constructor(private val appRepoImpl: AppRepoImpl) {

}