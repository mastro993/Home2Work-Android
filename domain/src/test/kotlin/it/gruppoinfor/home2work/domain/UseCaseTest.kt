package it.gruppoinfor.home2work.domain

import io.reactivex.Observable
import it.gruppoinfor.home2work.domain.common.DomainTestUtils.Companion.generateCompanyList
import it.gruppoinfor.home2work.domain.common.DomainTestUtils.Companion.generateUserList
import it.gruppoinfor.home2work.domain.common.TestTransformer
import it.gruppoinfor.home2work.domain.interfaces.CompanyRepository
import it.gruppoinfor.home2work.domain.interfaces.UserRepository
import it.gruppoinfor.home2work.domain.usecases.GetCompanyList
import it.gruppoinfor.home2work.domain.usecases.GetUserList
import org.junit.Test
import org.mockito.Mockito

class UseCaseTest {

    fun main(args: Array<Any>) {

    }

    @Test
    fun testGetCompanyList() {
        val companyRepository = Mockito.mock(CompanyRepository::class.java)
        Mockito.`when`(companyRepository.getCompanies()).thenReturn(Observable.just(generateCompanyList()))
        val getCompanyList = GetCompanyList(TestTransformer(), companyRepository)
        getCompanyList.observable().test()
                .assertValue { results -> results.size == 5 }
                .assertComplete()
    }

    @Test
    fun testGetUserList() {
        val userRepository = Mockito.mock(UserRepository::class.java)
        Mockito.`when`(userRepository.getUserList()).thenReturn(Observable.just(generateUserList()))
        val getUserList = GetUserList(TestTransformer(), userRepository)
        getUserList.observable().test()
                .assertValue { results -> results.size == 5 }
                .assertComplete()
    }
}