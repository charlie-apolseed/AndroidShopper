package com.example.apolinskyshoppingapp.ui.screen


import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apolinskyshoppingapp.data.ShoppingDAO
import com.example.apolinskyshoppingapp.data.ShoppingItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShoppingModel @Inject constructor(
    val shoppingDAO: ShoppingDAO
) : ViewModel() {


    fun getAllItems(): Flow<List<ShoppingItem>> {
        return shoppingDAO.getAllTodos()
    }

    fun addItem(shoppingItem: ShoppingItem) {
        viewModelScope.launch(Dispatchers.IO) {
            shoppingDAO.insert(shoppingItem)
        }
    }

    fun removeItem(shoppingItem: ShoppingItem) {
        //
    }

    fun editItem(originalShoppingItem: ShoppingItem, editedShoppingItem: ShoppingItem) {
        //
    }

    fun changeItemState(shoppingItem: ShoppingItem, value: Boolean) {
        //
    }

    fun clearShoppingItems() {
        //
    }
}
