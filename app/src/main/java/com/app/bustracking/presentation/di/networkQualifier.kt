package com.app.bustracking.presentation.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ApiServiceRegular

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ApiServiceWithAuth