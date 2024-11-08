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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.apolinskyshoppingapp.R
import com.example.apolinskyshoppingapp.data.ShoppingCategory
import com.example.apolinskyshoppingapp.data.ShoppingItem


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingScreen(
    modifier: Modifier = Modifier, viewModel: ShoppingModel = hiltViewModel()
) {
    val shoppingList by viewModel.getAllItems().collectAsState(emptyList())
    var showAddDialog by rememberSaveable { mutableStateOf(false) }
    var showDeleteAllDialog by rememberSaveable { mutableStateOf(false) }
    var shoppingToEdit: ShoppingItem? by rememberSaveable {
        mutableStateOf(null)
    }



    Scaffold(
        topBar = {
            TopAppBar(title = { Text(stringResource(R.string.apolinsky_shopper)) },
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
                            Icons.Filled.AddCircle, contentDescription = stringResource(R.string.add_new_item)
                        )
                    }
                    IconButton(
                        onClick = {
                            showDeleteAllDialog = true
                        }
                    ) {
                        Icon(
                            Icons.Filled.Delete, contentDescription = stringResource(R.string.delete_all_items)
                        )
                    }
                }
            )
        }
    ) { innerpadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerpadding)
        ) {
            if (shoppingList.isEmpty()) {
                Text(
                    stringResource(R.string.empty_list), modifier = Modifier
                        .fillMaxSize()
                        .wrapContentSize(Alignment.Center)
                )
            } else {
                LazyColumn {
                    items(shoppingList) { todoItem ->
                        ShoppingCard(todoItem,
                            onShoppingDelete = { item -> viewModel.removeItem(item) },
                            onShoppingEdit = { item ->
                                shoppingToEdit = item
                                showAddDialog = true
                            },
                            onShoppingChecked = { item, checked ->
                                viewModel.changeItemState(
                                    item,
                                    checked
                                )
                            }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        ShoppingDialog(
            viewModel,
            shoppingToEdit = shoppingToEdit,
            onCancel = {
                showAddDialog = false
                shoppingToEdit = null
            },
        )
    }
    if (showDeleteAllDialog) {
        DeleteAllDialog(
            onCancel = {
                showDeleteAllDialog = false
            },
            onConfirm = {
                viewModel.clearShoppingItems()
            }
        )
    }
}


private const val s = "Edit Shopping Item"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingDialog(
    viewModel: ShoppingModel,
    onCancel: () -> Unit,
    shoppingToEdit: ShoppingItem? = null
) {
    var priceErrorState by remember { mutableStateOf(false) }
    var priceErrorText by remember { mutableStateOf("") }
    var nameErrorState by remember { mutableStateOf(false) }
    var nameErrorText by remember { mutableStateOf("") }

    var itemName by remember { mutableStateOf(shoppingToEdit?.name ?: "") }
    var itemDescription by remember { mutableStateOf(shoppingToEdit?.description ?: "") }
    var itemPrice by remember { mutableStateOf(shoppingToEdit?.price ?: "") }
    var itemCategory by remember {
        mutableStateOf(
            shoppingToEdit?.category ?: ShoppingCategory.Food
        )
    }
    var itemPurchased by remember { mutableStateOf(shoppingToEdit?.purchased ?: false) }

    var categoriesExpanded by remember { mutableStateOf(false) }
    var selectedCategory by remember {
        mutableStateOf(
            shoppingToEdit?.category?.name ?: "Food"
        )
    }

    fun validateInput(input: String) {
        try {
            input.toFloat()
            val decimalRegex = Regex("""^\d+(\.\d{1,2})?$""")
            if (!decimalRegex.matches(input)) {
                priceErrorState = true
                priceErrorText = "Input can only have up to two decimal places"
            } else {
                priceErrorState = false
            }
        } catch (e: Exception) {
            priceErrorText = e.localizedMessage!!
            priceErrorState = true
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
                Text(
                    text = if (shoppingToEdit == null) stringResource(R.string.add_shopping_item) else stringResource(
                        R.string.edit_shopping_item
                    ),
                    style = MaterialTheme.typography.titleMedium
                )

                OutlinedTextField(modifier = Modifier.fillMaxWidth(),
                    label = { Text(stringResource(R.string.name)) },
                    value = itemName,
                    onValueChange = {
                        itemName = it
                        nameErrorState = itemName.isEmpty()
                        nameErrorText = if (nameErrorState) "Please enter a name" else ""
                    },
                    isError = nameErrorState,
                    supportingText = {
                        if (nameErrorState) {
                            Text(nameErrorText)
                        }
                    },
                    trailingIcon = {
                        if (nameErrorState)
                            Icon(
                                Icons.Filled.Warning, "error",
                                tint = MaterialTheme.colorScheme.error
                            )
                    }
                )
                OutlinedTextField(modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 15.dp),
                    label = { Text(stringResource(R.string.description)) },
                    value = itemDescription,
                    onValueChange = { itemDescription = it })

                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(stringResource(R.string.price)) },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),

                    value = itemPrice,
                    onValueChange = {
                        itemPrice = it
                        validateInput(itemPrice)
                    },
                    isError = priceErrorState,
                    supportingText = {
                        if (priceErrorState) {
                            Text(stringResource(R.string.please_enter_a_valid_price))
                        }
                    },
                    trailingIcon = {
                        if (priceErrorState)
                            Icon(
                                Icons.Filled.Warning, "error",
                                tint = MaterialTheme.colorScheme.error
                            )
                    }
                )

                ExposedDropdownMenuBox(
                    expanded = categoriesExpanded,
                    onExpandedChange = { categoriesExpanded = it })
                {
                    TextField(
                        value = selectedCategory,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoriesExpanded) },
                        modifier = Modifier.menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = categoriesExpanded,
                        onDismissRequest = { categoriesExpanded = false })
                    {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.food)) },
                            onClick = {
                                selectedCategory = "Food"
                                categoriesExpanded = false
                                itemCategory = ShoppingCategory.Food
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.books)) },
                            onClick = {
                                selectedCategory = "Books"
                                categoriesExpanded = false
                                itemCategory = ShoppingCategory.Book
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.clothes)) },
                            onClick = {
                                selectedCategory = "Clothes"
                                categoriesExpanded = false
                                itemCategory = ShoppingCategory.Clothing
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.luxury)) },
                            onClick = {
                                selectedCategory = "Luxury"
                                categoriesExpanded = false
                                itemCategory = ShoppingCategory.Luxury
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.other)) },
                            onClick = {
                                selectedCategory = "Other"
                                categoriesExpanded = false
                                itemCategory = ShoppingCategory.Other
                            }
                        )

                    }
                }




                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Purchased")
                        Checkbox(
                            onCheckedChange = { itemPurchased = it },
                            checked = itemPurchased,
                        )
                    }
                    TextButton(onClick = {
                        if (itemName != "") {
                            if (itemPrice != "") {
                                if (shoppingToEdit != null) {
                                    viewModel.editItem(
                                        originalShoppingItem = shoppingToEdit,
                                        editedShoppingItem = ShoppingItem(
                                            id = 0,
                                            name = itemName,
                                            description = itemDescription,
                                            category = itemCategory,
                                            price = itemPrice,
                                            purchased = itemPurchased
                                        )
                                    )
                                } else {
                                    viewModel.addItem(
                                        ShoppingItem(
                                            id = 0,
                                            name = itemName,
                                            description = itemDescription,
                                            category = itemCategory,
                                            price = itemPrice,
                                            purchased = itemPurchased
                                        )
                                    )
                                }
                                onCancel()
                            } else {
                                priceErrorState = itemPrice.isEmpty()
                                priceErrorText = if (priceErrorState) "Please enter a price" else ""
                            }
                        } else {
                            nameErrorState = itemName.isEmpty()
                            nameErrorText = if (nameErrorState) "Please enter a name" else ""
                            priceErrorState = itemPrice.isEmpty()
                            priceErrorText =
                                if (nameErrorState) "Please enter a valid price" else "" //TODO how do I extract these?
                        }

                    }) {
                        Text(stringResource(R.string.confirm))
                    }
                }
            }
        }
    }
}

@Composable
fun DeleteAllDialog(
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
) {
    Dialog(onDismissRequest = {
        onCancel()
    }) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(size = 6.dp)
        ) {
            Column(modifier = Modifier.padding(15.dp)) {
                Text(
                    text = stringResource(R.string.delete_all_items),
                    style = MaterialTheme.typography.titleMedium
                )
                Text(stringResource(R.string.confirm_delete_all))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(onClick = { onCancel() }) {
                        Text(stringResource(R.string.cancel))
                    }
                    TextButton(onClick = {
                        onConfirm()
                        onCancel()
                    }) {
                        Text(stringResource(R.string.confirm))
                    }
                }
            }
        }
    }
}


@Composable
fun ShoppingCard(
    shoppingItem: ShoppingItem,
    onShoppingDelete: (ShoppingItem) -> Unit,
    onShoppingEdit: (ShoppingItem) -> Unit,
    onShoppingChecked: (ShoppingItem, checked: Boolean) -> Unit
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
        var itemChecked by remember { mutableStateOf(shoppingItem.purchased) }

        Column(
            modifier = Modifier
                .padding(20.dp)
                .animateContentSize()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = shoppingItem.category.getIcon()),
                    contentDescription = "Icon",
                    modifier = Modifier
                        .size(40.dp)
                        .padding(end = 10.dp)
                )

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = shoppingItem.name, textDecoration = if (shoppingItem.purchased) {
                            TextDecoration.LineThrough
                        } else {
                            TextDecoration.None
                        }
                    )
                    Text(
                        text = "$ " + shoppingItem.price,
                        style = TextStyle(
                            fontSize = 12.sp,
                        )
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = shoppingItem.purchased,
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
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "Edit",
                        modifier = Modifier
                            .padding(start = 10.dp)
                            .clickable {
                                onShoppingEdit(shoppingItem)
                            },
                        tint = Color.Black
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
            }

        }
    }
}