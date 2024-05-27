package com.example.stockmarket.data.repository

import android.net.http.HttpException
import android.os.Build
import androidx.annotation.RequiresExtension
import com.example.stockmarket.data.local.StockDatabase
import com.example.stockmarket.data.mapper.toCompanyListing
import com.example.stockmarket.data.remote.StockApi
import com.example.stockmarket.domain.model.CompanyListing
import com.example.stockmarket.domain.repository.StockRepository
import com.example.stockmarket.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StockRepositoryImpl @Inject constructor(
    val api: StockApi,
    db: StockDatabase
) : StockRepository {

    private val dao = db.dao

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    override suspend fun getCompanyListings(
        fetchFromRemote: Boolean,
        query: String
    ): Flow<Resource<List<CompanyListing>>> {
        return flow {
            emit(Resource.Loading(true))
            val localListings = dao.searchCompanyListing(query)
            emit(Resource.Success(
                data = localListings.map { it.toCompanyListing() }
            ))
            val isDbEmpty = localListings.isEmpty() && query.isBlank()
            val shouldJustLoadFromCache = !isDbEmpty && !fetchFromRemote
            if (shouldJustLoadFromCache) {
                emit(Resource.Loading(false))
                return@flow
            }
            val remoteListing = try {
                val response = api.getListings()

            } catch (e: IOException) {
                e.printStackTrace()
                emit(Resource.Error("Could not load data"))
            } catch (e: HttpException) {

                e.printStackTrace()
                emit(Resource.Error("Could not load data"))
            }
        }
    }
}