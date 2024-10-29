package com.example.apolinskyshoppingapp.ui.screen

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.apolinskyshoppingapp.data.ShoppingCategory
import com.example.apolinskyshoppingapp.data.ShoppingItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingScreen(
    modifier: Modifier = Modifier, viewModel: ShoppingModel = hiltViewModel()
) {
    val shoppingList by viewModel.getAllItems().collectAsState(emptyList())

    var showAddDialog by remember { mutableStateOf(false) }



    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Apolinsky Shopper") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    actionIconContentColor = MaterialTheme.colorScheme.onSecondaryContainer
                ),
                actions = {
                    IconButton(
                        onClick = {
                            showAddDialog = true
                        }
                    ) {
                        Icon(
                            Icons.Filled.AddCircle, contentDescription = "Add New Item"
                        )
                    }
                }
            )
        }
    ) { innerpadding ->
        Column(modifier = modifier
            .fillMaxSize()
            .padding(innerpadding)) {
            if (shoppingList.isEmpty()) {
                Text(
                    "Empty list", modifier = Modifier
                        .fillMaxSize()
                        .wrapContentSize(Alignment.Center)
                )
            } else {
                LazyColumn {
                    items(shoppingList) { todoItem ->
                        ShoppingCard(todoItem,
                            onShoppingDelete = {},
                            onShoppingChecked = {item, checked -> viewModel.changeItemState(item, checked)}
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        ShoppingDialog(viewModel,
            onCancel = { showAddDialog = false }
        )
    }
}



@Composable
fun ShoppingDialog(
    viewModel: ShoppingModel,
    onCancel: () -> Unit
) {
    var inputErrorState by remember { mutableStateOf(false) }
    var errorText by remember { mutableStateOf("") }
    var itemName by remember { mutableStateOf("") }
    var itemDescription by remember { mutableStateOf("") }
    var itemPrice by remember { mutableStateOf("") }
    var itemCategory by remember { mutableStateOf(ShoppingCategory.Other) }



    fun validateInput(input: String) {
        try {
            val myNum = input.toInt()
            inputErrorState = false
        } catch (e: Exception) {
            errorText = e.localizedMessage!!
            inputErrorState = true
        }
    }

    Dialog(onDismissRequest = {
        onCancel()
    }) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(size = 6.dp)
        ) {
            Column(
                modifier = Modifier.padding(15.dp)
            ) {
                Text("Add Item",
                    style = MaterialTheme.typography.titleMedium)

                OutlinedTextField(modifier = Modifier.fillMaxWidth(),
                    label = { Text("Name") },
                    value = itemName,
                    onValueChange = { itemName = it })
                OutlinedTextField(modifier = Modifier.fillMaxWidth(),
                    label = { Text("Description") },
                    value = itemDescription,
                    onValueChange = { itemDescription = it })
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Price") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),

                    value = itemPrice,
                    onValueChange = {
                        //if (it.length <=3) {
                        itemPrice = it
                        validateInput(itemPrice)
                        //}
                    },
                    isError = inputErrorState,
                    supportingText = {
                        if (inputErrorState) {
                            Text(errorText)
                        }
                    },
                    trailingIcon = {
                        if (inputErrorState)
                            Icon(Icons.Filled.Warning, "error",
                                tint = MaterialTheme.colorScheme.error)
                    }
                )



                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = {
                        viewModel.addItem(
                            ShoppingItem(
                                id = 0,
                                name = itemName,
                                description = itemDescription,
                                category = itemCategory,
                                price = itemPrice,
                                status = false
                            )
                        )

                        onCancel()
                    }) {
                        Text("Add Item")
                    }
                }
            }
        }
    }
}


@Composable
fun ShoppingCard(shoppingItem: ShoppingItem,
             onShoppingDelete: (ShoppingItem) -> Unit,
             onShoppingChecked: (ShoppingItem, checked : Boolean) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ), shape = RoundedCornerShape(20.dp), elevation = CardDefaults.cardElevation(
            defaultElevation = 10.dp
        ), modifier = Modifier
            .padding(5.dp)
            .fillMaxWidth()
    ) {
        var expanded by remember { mutableStateOf(false) }
        var itemChecked by remember { mutableStateOf(shoppingItem.status) }

        Column(
            modifier = Modifier
                .padding(20.dp)
                .animateContentSize()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
//                Image(
//                    painter = painterResource(id = shoppingItem.category.getIcon()),
//                    contentDescription = "Priority",
//                    modifier = Modifier
//                        .size(40.dp)
//                        .padding(end = 10.dp)
//                )

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = shoppingItem.name, textDecoration = if (shoppingItem.status) {
                            TextDecoration.LineThrough
                        } else {
                            TextDecoration.None
                        }
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = shoppingItem.status,
                        onCheckedChange = {
                            itemChecked = it
                            onShoppingChecked(shoppingItem, itemChecked)
                        },
                    )
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Delete",
                        modifier = Modifier.clickable {
                            onShoppingDelete(shoppingItem)
                        },
                        tint = Color.Red
                    )
                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(
                            imageVector = if (expanded) Icons.Filled.KeyboardArrowUp
                            else Icons.Filled.KeyboardArrowDown,
                            contentDescription = if (expanded) {
                                "Less"
                            } else {
                                "More"
                            }
                        )
                    }
                }
            }

            if (expanded) {
                Text(
                    text = shoppingItem.description,
                    style = TextStyle(
                        fontSize = 12.sp,
                    )
                )
                Text(
                    text = shoppingItem.price,
                    style = TextStyle(
                        fontSize = 12.sp,
                    )
                )
            }

        }
    }
}