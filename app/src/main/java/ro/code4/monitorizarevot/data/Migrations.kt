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

    /**
     * This migration changes the database to add the new orderNumber for sections, questions and answers.
     */
    val MIGRATION_3_4 = object : Migration(3, 4) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE section ADD COLUMN `orderNumber` INTEGER NOT NULL DEFAULT 0")
            database.execSQL("ALTER TABLE question ADD COLUMN `orderNumber` INTEGER NOT NULL DEFAULT 0")
            database.execSQL("ALTER TABLE answer ADD COLUMN `orderNumber` INTEGER NOT NULL DEFAULT 0")
        }
    }

    /**
     * This migration changes the database to add the form and question codes to the Note entity.
     */
    val MIGRATION_4_5 = object : Migration(4, 5) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE note ADD COLUMN `formCode` TEXT DEFAULT NULL")
            database.execSQL("ALTER TABLE note ADD COLUMN `questionCode` TEXT DEFAULT NULL")
        }
    }

    val ALL: Array<Migration> = arrayOf(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5)
}