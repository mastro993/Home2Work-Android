package it.gruppoinfor.home2work.domain.interfaces

import io.reactivex.Observable
import it.gruppoinfor.home2work.domain.entities.CompanyEntity
import it.gruppoinfor.home2work.domain.entities.Optional


interface CompanyRepository {
    fun getCompanies(): Observable<List<CompanyEntity>>
    fun getCompany(companyId: Long): Observable<Optional<CompanyEntity>>
}