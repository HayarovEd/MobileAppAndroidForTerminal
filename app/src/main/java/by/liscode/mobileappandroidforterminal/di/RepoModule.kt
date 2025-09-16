package by.liscode.mobileappandroidforterminal.di


import by.liscode.data.repository.AuthorizeRepositoryImpl
import by.liscode.data.repository.DataStoreRepositoryImpl
import by.liscode.data.repository.JwtRepositoryImpl
import by.liscode.data.repository.LocalRepositoryImpl
import by.liscode.data.repository.RemoteRepositoryImpl
import by.liscode.domain.repository.AuthorizeRepository
import by.liscode.domain.repository.DataStoreRepository
import by.liscode.domain.repository.JwtRepository
import by.liscode.domain.repository.LocalRepository
import by.liscode.domain.repository.RemoteRepository
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val repoModule = module {
   // singleOf(::RemoteRepositoryImpl) { bind<RemoteRepository>() }
    singleOf(::DataStoreRepositoryImpl) { bind<DataStoreRepository>() }
    singleOf(::LocalRepositoryImpl) { bind<LocalRepository>() }
    singleOf(::AuthorizeRepositoryImpl) { bind<AuthorizeRepository>() }
    singleOf(::JwtRepositoryImpl) { bind<JwtRepository>() }
    singleOf(::RemoteRepositoryImpl) { bind<RemoteRepository>() }

}
