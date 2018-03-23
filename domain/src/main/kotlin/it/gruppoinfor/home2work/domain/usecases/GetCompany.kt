package it.gruppoinfor.home2work.domain.usecases

import io.reactivex.Observable
import it.gruppoinfor.home2work.domain.common.Transformer
import it.gruppoinfor.home2work.domain.entities.CompanyEntity
import it.gruppoinfor.home2work.domain.entities.Optional
import it.gruppoinfor.home2work.domain.interfaces.CompanyRepository

class GetCompany(
        transformer: Transformer<Optional<CompanyEntity>>,
        private val companyRepository: CompanyRepository
) : UseCase<Optional<CompanyEntity>>(transformer) {

    companion object {
        private const val PARAM_COMPANY_ID = "param:companyId"
    }

    fun getById(companyId: Long): Observable<Optional<CompanyEntity>>{
        val data = HashMap<String, Long>()
        data[PARAM_COMPANY_ID] = companyId
        return observable(data)
    }

    override fun createObservable(data: Map<String, Any>?): Observable<Optional<CompanyEntity>> {
        val companyId = data?.get(PARAM_COMPANY_ID)
        companyId?.let {
            return companyRepository.getCompany(companyId as Long)
        } ?: return Observable.error(IllegalArgumentException("companyId must be provided."))
    }
}