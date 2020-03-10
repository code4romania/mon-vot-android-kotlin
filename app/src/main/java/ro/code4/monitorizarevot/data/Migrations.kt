package ro.code4.monitorizarevot.data

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object Migrations {
    /*
    According to https://developer.android.com/reference/android/database/sqlite/package-summary Android has old SQLite.
    For example, you can't rename column without recreating table
     */

    // this is an example for migration
    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE county ADD COLUMN `diaspora` INTEGER DEFAULT NULL")
            database.execSQL("ALTER TABLE form_details ADD COLUMN `diaspora` INTEGER DEFAULT NULL")
            database.execSQL("ALTER TABLE form_details ADD COLUMN `order` INTEGER NOT NULL DEFAULT 0")
        }
    }

    val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE county ADD COLUMN `order` INTEGER NOT NULL DEFAULT 0")
        }
    }

    val ALL: Array<Migration> = arrayOf(MIGRATION_1_2, MIGRATION_2_3)
}