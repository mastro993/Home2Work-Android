package it.gruppoinfor.home2work.data

import io.reactivex.schedulers.Schedulers
import it.gruppoinfor.home2work.data.mappers.ProfileDataEntityMapper
import it.gruppoinfor.home2work.data.mappers.UserDataEntityMapper
import it.gruppoinfor.home2work.data.repositories.UserRepositoryImpl
import it.gruppoinfor.home2work.domain.interfaces.UserRepository
import org.junit.Before
import org.junit.Test


class UserRepositoryImplTest {
    private val userMapper = UserDataEntityMapper()
    private val profileMapper = ProfileDataEntityMapper()
    private lateinit var userRepo: UserRepository

    @Before
    fun before() {
        userRepo = UserRepositoryImpl(
                userMapper,
                profileMapper
        )
    }

    @Test
    fun loginTest() {
        userRepo.login("utente1@test.com", "123456")
                .test()
                .assertValue {
                    it.hasValue()
                }
                .assertComplete()
    }
}