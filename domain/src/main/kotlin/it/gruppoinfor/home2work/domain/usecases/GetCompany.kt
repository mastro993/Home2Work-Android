package it.gruppoinfor.home2work.domain.usecases

import io.reactivex.Observable
import it.gruppoinfor.home2work.domain.common.Transformer
import it.gruppoinfor.home2work.domain.entities.Company
import it.gruppoinfor.home2work.domain.interfaces.CompanyRepository

class GetCompany(
        transformer: Transformer<Company>,
        private val companyRepository: CompanyRepository
) : UseCase<Company>(transformer) {

    companion object {
        private const val PARAM_COMPANY_ID = "param:companyId"
    }

    override fun createObservable(data: Map<String, Any>?): Observable<Company> {
        val companyId = data?.get(PARAM_COMPANY_ID)
        companyId?.let {
            return companyRepository.getCompany(companyId as Long)
        } ?: return Observable.error(IllegalArgumentException("companyId must be provided."))
    }
}