package by.liscode.fund

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import by.liscode.domain.repository.DataStoreRepository
import by.liscode.domain.repository.JwtRepository
import by.liscode.resources.model.fundRoutes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FundScreenViewModel(
    private val dataStoreRepository: DataStoreRepository,
    private val jwtRepository: JwtRepository,
) : ViewModel() {


    private val _state = MutableStateFlow(FundScreenState())
    val state = _state
        .onStart {
            getInitialData()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = FundScreenState()
        )


    private fun getInitialData() {
        viewModelScope.launch {
            dataStoreRepository.getBearer().collect { token ->
                token?.let {
                    val tokenContent = jwtRepository.decodeJwt(it)
                    tokenContent?.let { content ->
                        _state.value.copy(
                            routes = fundRoutes.filter { fund ->
                                if (tokenContent.userType == null) {
                                    fund.accessedRoles.contains(tokenContent.userRole)
                                } else {
                                    fund.accessedRoles.contains(tokenContent.userRole) && fund.accessedTypes.contains(
                                        tokenContent.userType
                                    )
                                }
                            }
                        )
                            .updateState()
                    }
                }
            }
        }
    }


    private fun FundScreenState.updateState() {
        _state.update {
            this
        }
    }
}

