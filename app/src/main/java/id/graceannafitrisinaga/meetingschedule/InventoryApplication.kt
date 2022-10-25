package id.graceannafitrisinaga.meetingschedule

import android.app.Application
import id.graceannafitrisinaga.meetingschedule.data.ItemRoomDatabase

class InventoryApplication : Application() {
    // Menggunakan oleh lazy sehingga database hanya dibuat saat dibutuhkan
    // daripada saat aplikasi dimulai
    val database: ItemRoomDatabase by lazy { ItemRoomDatabase.getDatabase(this) }
}
