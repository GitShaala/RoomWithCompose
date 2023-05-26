package com.app.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactDao {

    @Upsert
    suspend fun addContact(contact: Contact)
    @Delete
    suspend fun deleteContact(contact: Contact)

    @Query("SELECT * from contact ORDER BY firstName ASC")
    fun sortContactsByFirstName():Flow<List<Contact>>

    @Query("SELECT * from contact ORDER BY lastName ASC")
    fun sortContactsByLastName():Flow<List<Contact>>

    @Query("SELECT * from contact ORDER BY phoneNumber ASC")
    fun sortContactsByPhoneNumber():Flow<List<Contact>>


}