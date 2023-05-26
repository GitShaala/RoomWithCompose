package com.app.room

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class MainViewModel @Inject constructor(private val contactDao: ContactDao) : ViewModel() {

    private val _sortType = MutableStateFlow(SortType.FIRST_NAME)
    private val _contacts = _sortType.flatMapLatest {
        when (it) {
            SortType.FIRST_NAME -> contactDao.sortContactsByFirstName()
            SortType.LAST_NAME -> contactDao.sortContactsByLastName()
            SortType.PHONE_NUMBER -> contactDao.sortContactsByPhoneNumber()
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
    }

    private val _state = MutableStateFlow(ContactState())
    val state = combine(_state, _sortType, _contacts) { state, sortType, contacts ->
        state.copy(contacts = contacts, sortType = sortType)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), ContactState())

    fun onEvent(event: ContactEvent) {
        when (event) {
            ContactEvent.SaveContact -> {
                val firstName = state.value.firstName
                val lastName = state.value.lastName
                val phoneNumber = state.value.phoneNumber

                if (firstName.isEmpty() || lastName.isEmpty() || phoneNumber.isEmpty()) {
                    return
                }
                val contact =
                    Contact(
                        firstName = firstName,
                        lastName = lastName,
                        phoneNumber = phoneNumber
                    )

                viewModelScope.launch {
                    contactDao.addContact(contact)
                }

                _state.update {
                    it.copy(
                        firstName = "",
                        lastName = "",
                        phoneNumber = "",
                        isAddingContact = false
                    )
                }
            }

            is ContactEvent.SetFirstName -> {
                _state.update {
                    it.copy(firstName = event.firstName)
                }
            }

            is ContactEvent.SetLastName -> {
                _state.update {
                    it.copy(lastName = event.lastName)
                }
            }

            is ContactEvent.SetPhoneNumber -> {
                _state.update {
                    it.copy(phoneNumber = event.phoneNumber)
                }
            }

            ContactEvent.HideDialog -> {
                _state.update {
                    it.copy(isAddingContact = false)
                }
            }

            ContactEvent.ShowDialog -> {
                _state.update {
                    it.copy(isAddingContact = true)
                }
            }

            is ContactEvent.DeleteContact -> {
                viewModelScope.launch {
                    contactDao.deleteContact(event.contact)
                }
            }

            is ContactEvent.Sort -> {
                _sortType.value = event.sort
            }
        }
    }

}