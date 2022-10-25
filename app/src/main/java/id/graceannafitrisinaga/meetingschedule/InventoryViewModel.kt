package id.graceannafitrisinaga.meetingschedule

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope

import id.graceannafitrisinaga.meetingschedule.data.Item
import id.graceannafitrisinaga.meetingschedule.data.ItemDao
import kotlinx.coroutines.launch

/**
 * Lihat Model untuk menyimpan referensi ke repositori Schedule dan daftar terbaru dari semua item.
 *
 */
class InventoryViewModel(private val itemDao: ItemDao) : ViewModel() {

    // Cache all items form the database using LiveData.
    val allItems: LiveData<List<Item>> = itemDao.getItems().asLiveData()

    /**
     * Memperbarui Item yang ada di database.
     */
    fun updateItem(
        itemId: Int,
        itemName: String,
        itemCount: String
    ) {
        val updatedItem = getUpdatedItemEntry(itemId, itemName, itemCount)
        updateItem(updatedItem)
    }


    /**
     * Meluncurkan coroutine baru untuk memperbarui item dengan cara yang tidak memblokir
     */
    private fun updateItem(item: Item) {
        viewModelScope.launch {
            itemDao.update(item)
        }
    }

    /**
     * Menyisipkan Item baru ke dalam database.
     */
    fun addNewItem(itemName: String, itemCount: String) {
        val newItem = getNewItemEntry(itemName, itemCount)
        insertItem(newItem)
    }

    /**
     * Meluncurkan coroutine baru untuk menyisipkan item dengan cara yang tidak memblokir
     */
    private fun insertItem(item: Item) {
        viewModelScope.launch {
            itemDao.insert(item)
        }
    }

    /**
     * Meluncurkan coroutine baru untuk menghapus item dengan cara yang tidak memblokir
     */
    fun deleteItem(item: Item) {
        viewModelScope.launch {
            itemDao.delete(item)
        }
    }

    /**
     * Ambil item dari repositori.
     */
    fun retrieveItem(id: Int): LiveData<Item> {
        return itemDao.getItem(id).asLiveData()
    }

    /**
     * Mengembalikan nilai true jika EditTexts tidak kosong
     */
    fun isEntryValid(itemName: String, itemCount: String): Boolean {
        if (itemName.isBlank() || itemCount.isBlank()) {
            return false
        }
        return true
    }

    /**
     * Mengembalikan instance kelas entitas [Item] dengan info item yang dimasukkan oleh pengguna.
     * Ini akan digunakan untuk menambahkan entri baru ke database Schedule.
     */
    private fun getNewItemEntry(itemName: String, itemCount: String): Item {
        return Item(
            itemName = itemName,
            quantityInStock = itemCount.toInt()
        )
    }

    /**
     * Dipanggil untuk memperbarui entri yang ada di database Schedule.
     * Mengembalikan instance kelas entitas [Item] dengan info item yang diperbarui oleh pengguna.
     */
    private fun getUpdatedItemEntry(
        itemId: Int,
        itemName: String,
        itemCount: String
    ): Item {
        return Item(
            id = itemId,
            itemName = itemName,
            quantityInStock = itemCount.toInt()
        )
    }

}

/**
 * Kelas pabrik untuk membuat instance [ViewModel].
 */
class InventoryViewModelFactory(private val itemDao: ItemDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InventoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return InventoryViewModel(itemDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

