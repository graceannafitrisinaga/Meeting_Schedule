package id.graceannafitrisinaga.meetingschedule.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Kelas database dengan objek INSTANCE tunggal.
 */
@Database(entities = [Item::class], version = 1, exportSchema = false)
abstract class ItemRoomDatabase : RoomDatabase() {

    abstract fun itemDao(): ItemDao

    companion object {
        @Volatile
        private var INSTANCE: ItemRoomDatabase? = null

        fun getDatabase(context: Context): ItemRoomDatabase {
            // sebuah INSTANCE bukan null, dikembalikan,
            //jika ya, buat databasenya
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ItemRoomDatabase::class.java,
                    "item_database"
                )

                // Menghapus dan membangun kembali alih-alih bermigrasi jika tidak ada objek Migrasi.
                    // Migrasi bukan bagian dari codelab ini.
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                // kembalikan instance
                instance
            }
        }
    }
}