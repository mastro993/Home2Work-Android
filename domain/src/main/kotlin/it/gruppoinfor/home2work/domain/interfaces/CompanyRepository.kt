package it.gruppoinfor.home2work.domain.interfaces

import io.reactivex.Observable
import it.gruppoinfor.home2work.domain.entities.Company


interface CompanyRepository {
    fun getCompanies(): Observable<List<Company>>
    fun getCompany(companyId: Long): Observable<Company>
}