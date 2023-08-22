package id.teman.app.mitra.manager

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

interface UserManager {
    suspend fun observeUserState(): Flow<UserState>
    suspend fun changeUserState(state: UserState)
    suspend fun start()
}

class DefaultUserManager: UserManager {
    private val _userFlow = MutableStateFlow<UserState>(UserState.Active)
    override suspend fun observeUserState(): Flow<UserState> {
        return _userFlow
    }


    override suspend fun changeUserState(state: UserState) {
        _userFlow.emit(state)
    }

    override suspend fun start() {
        _userFlow.emit(UserState.Active)
    }

}

sealed class UserState {
    object Revoked: UserState()
    object Active: UserState()
}