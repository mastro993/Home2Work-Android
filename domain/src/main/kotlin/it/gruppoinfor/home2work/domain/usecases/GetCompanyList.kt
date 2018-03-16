package it.gruppoinfor.home2work.domain.usecases

import io.reactivex.Observable
import it.gruppoinfor.home2work.domain.common.Transformer
import it.gruppoinfor.home2work.domain.entities.CompanyEntity
import it.gruppoinfor.home2work.domain.entities.LatLngEntity
import it.gruppoinfor.home2work.domain.interfaces.CompanyRepository


class GetCompanyList(
        transformer: Transformer<List<CompanyEntity>>,
        private val companyRepository: CompanyRepository
) : UseCase<List<CompanyEntity>>(transformer) {

    override fun createObservable(data: Map<String, Any>?): Observable<List<CompanyEntity>> {
        return companyRepository.getCompanies()
    }
}