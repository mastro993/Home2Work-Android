package it.gruppoinfor.home2workapi.company

import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path


interface CompanyService {
    @get:GET("company/list")
    val companies: Observable<List<Company>>

    @GET("company/{id}")
    fun getCompanyById(
            @Path("id") id: Long?
    ): Observable<Company>

    @GET("company/{id}/profile")
    fun getCompanyProfileById(
            @Path("id") id: Long?
    ): Observable<Company>
}