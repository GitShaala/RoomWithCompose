package com.app.room.ui


import android.content.res.Resources.Theme
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Shapes
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.room.ContactEvent
import com.app.room.ContactState
import com.app.room.SortType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactScreen(state: ContactState, onEvent: (ContactEvent) -> Unit) {
    Scaffold(floatingActionButton = {
        FloatingActionButton(onClick = { onEvent(ContactEvent.ShowDialog) }, containerColor = Color.Cyan, shape = CircleShape, modifier = Modifier.padding(end = 20.dp, bottom = 10.dp)) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "add a contact")
        }
    }) { padding ->
        if (state.isAddingContact) {
            //Add Contact Dialog
            // onEvent(ContactEvent.ShowDialog)
            AddContactDialog(state = state, onEvent = onEvent)
        }

        LazyColumn(
            contentPadding = PaddingValues(8.dp),
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                        .horizontalScroll(rememberScrollState()),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SortType.values().forEach { sortType ->
                        Row(
                            modifier = Modifier.padding(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = sortType == state.sortType,
                                onClick = { onEvent(ContactEvent.Sort(sortType)) })

                            Text(text = sortType.name)
                        }
                    }
                }
            }

            items(state.contacts) { contact ->
                Card(
                    elevation = CardDefaults.cardElevation(6.dp), colors = CardDefaults.cardColors(
                        Color.White
                    ), modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(1.0f),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "${contact.firstName} ${contact.lastName}",
                                fontSize = 22.sp,
                                color = Color.Black
                            )
                            Text(
                                text = contact.phoneNumber,
                                fontSize = 16.sp,
                                color = Color.DarkGray
                            )
                        }

                        IconButton(onClick = { onEvent(ContactEvent.DeleteContact(contact)) }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "delete a contact"
                            )
                        }
                    }


                }
            }
        }
    }
}