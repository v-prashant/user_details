package com.example.trendingapp.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class HeaderHttpLoggingInterceptor

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class BodyHttpLoggingInterceptor

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ApiRetrialInterceptor

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DefaultInterceptor