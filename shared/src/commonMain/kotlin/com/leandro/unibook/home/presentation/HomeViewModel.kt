package com.leandro.unibook.home.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leandro.unibook.attendance.domain.usecase.GetAttendancesUseCase
import com.leandro.unibook.home.data.ChurchSettingsRepository
import com.leandro.unibook.home.data.UserProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getAttendances: GetAttendancesUseCase,
    private val churchSettingsRepository: ChurchSettingsRepository? = null,
    private val userProfileRepository: UserProfileRepository? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    var churchName by mutableStateOf(churchSettingsRepository?.getChurchName() ?: "Igreja Central - Sede")
        private set

    var churchPhotoBase64 by mutableStateOf(churchSettingsRepository?.getChurchPhoto() ?: "")
        private set
    var userNickname by mutableStateOf(userProfileRepository?.getNickname().orEmpty())
        private set
    var userMinistry by mutableStateOf(userProfileRepository?.getMinistry().orEmpty())
        private set

    init {
        observeAttendances()
        loadAttendances()
    }

    fun updateChurchProfile(name: String, photoBase64: String) {
        churchSettingsRepository?.saveChurchName(name)
        churchSettingsRepository?.saveChurchPhoto(photoBase64)
        churchName = name
        churchPhotoBase64 = photoBase64
    }

    fun updateUserProfile(nickname: String, ministry: String) {
        userProfileRepository?.saveProfile(nickname, ministry)
        userNickname = nickname
        userMinistry = ministry
    }

    private fun observeAttendances() {
        viewModelScope.launch {
            getAttendances.observe()
                .catch { e -> _uiState.value = HomeUiState.Error(e.message ?: "Erro ao carregar dados locais.") }
                .collect { list ->
                    _uiState.value = HomeUiState.Success(list)
                }
        }
    }

    fun loadAttendances(forceFetch: Boolean = false) {
        viewModelScope.launch {
            try {
                getAttendances(forceFetch)
            } catch (e: Exception) {
                // Erro de rede tratado graciosamente exibindo dados locais salvos
            }
        }
    }
}
