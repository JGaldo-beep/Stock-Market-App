package com.example.stockmarket.domain.repository

import com.example.stockmarket.domain.model.CompanyListing
import com.example.stockmarket.util.Resource
import kotlinx.coroutines.flow.Flow

// Domain Layer should not access the data layer
interface StockRepository {

    suspend fun getCompanyListings(
        fetchFromRemote: Boolean,
        query: String
    ) : Flow<Resource<List<CompanyListing>>>
}