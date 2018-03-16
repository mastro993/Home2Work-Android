package it.gruppoinfor.home2work.data.api.services

import io.reactivex.Observable
import it.gruppoinfor.home2work.domain.entities.CompanyEntity
import retrofit2.http.GET
import retrofit2.http.Path


interface CompanyService {
    @get:GET("company/list")
    val companies: Observable<List<CompanyEntity>>

    @GET("company/{id}")
    fun getCompanyById(
            @Path("id") id: Long?
    ): Observable<CompanyEntity>

    @GET("company/{id}/profile")
    fun getCompanyProfileById(
            @Path("id") id: Long?
    ): Observable<CompanyEntity>
}