package it.gruppoinfor.home2workapi.service

import io.reactivex.Observable
import it.gruppoinfor.home2workapi.model.Company
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path


interface CompanyService {
    @get:GET("company/list")
    val companies: Observable<Response<ArrayList<Company>>>

    @GET("company/{id}")
    fun getCompanyById(
            @Path("id") id: Long?
    ): Observable<Response<Company>>

    @GET("company/{id}/profile")
    fun getCompanyProfileById(
            @Path("id") id: Long?
    ): Observable<Response<Company>>
}