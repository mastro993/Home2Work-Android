package it.gruppoinfor.home2work.domain.usecases

import io.reactivex.Observable
import it.gruppoinfor.home2work.domain.common.Transformer
import it.gruppoinfor.home2work.domain.entities.Company
import it.gruppoinfor.home2work.domain.interfaces.CompanyRepository


class GetCompanyList(
        transformer: Transformer<List<Company>>,
        private val companyRepository: CompanyRepository
) : UseCase<List<Company>>(transformer) {

    override fun createObservable(data: Map<String, Any>?): Observable<List<Company>> {
        return companyRepository.getCompanies()
    }
}