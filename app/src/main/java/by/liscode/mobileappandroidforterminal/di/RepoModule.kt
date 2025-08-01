package by.liscode.mobileappandroidforterminal.di


import by.liscode.data.repository.DataStoreRepositoryImpl
import by.liscode.data.repository.LocalRepositoryImpl
import by.liscode.domain.repository.DataStoreRepository
import by.liscode.domain.repository.LocalRepository
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val repoModule = module {
   // singleOf(::RemoteRepositoryImpl) { bind<RemoteRepository>() }
    singleOf(::DataStoreRepositoryImpl) { bind<DataStoreRepository>() }
    singleOf(::LocalRepositoryImpl) { bind<LocalRepository>() }

}
