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
            database.execSQL("ALTER TABLE note ADD COLUMN `someNewField` INTEGER NOT NULL DEFAULT -1")
        }
    }

    val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // We want to remove column, it requires recreating table
            val commands = arrayOf(
                "PRAGMA foreign_keys=OFF",
                "BEGIN TRANSACTION;",
                // It can be found in schema json file
                """CREATE TABLE note_backup (
                    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
                    `uriPath` TEXT, 
                    `description` TEXT NOT NULL, 
                    `questionId` INTEGER, 
                    `date` INTEGER NOT NULL, 
                    `countyCode` TEXT NOT NULL, 
                    `branchNumber` INTEGER NOT NULL, 
                    `synced` INTEGER NOT NULL, 
                    FOREIGN KEY(`questionId`) REFERENCES `question`(`id`) 
                    ON UPDATE NO ACTION ON DELETE CASCADE, 
                    FOREIGN KEY(`countyCode`, `branchNumber`) REFERENCES `branch_details`(`countyCode`, `branchNumber`) 
                    ON UPDATE NO ACTION ON DELETE NO ACTION );                    
                """.trimIndent(),
                "INSERT INTO note_backup SELECT `id`, `uriPath`, `description`, `questionId`, `date`, `countyCode`, `branchNumber`, `synced` FROM note;",
                "DROP TABLE note;",
                "ALTER TABLE note_backup RENAME TO note;",
                // Don't forget about indices
                "CREATE INDEX `index_note_countyCode_branchNumber_questionId` ON `note` (`countyCode`, `branchNumber`, `questionId`);",
                "COMMIT;",
                "PRAGMA foreign_keys=ON"
            )
            for (command in commands)
                database.execSQL(command)
        }
    }

    // Room can handle more than one version increment: we can define a migration that makes the migration process faster.
    val MIGRATION_1_3 = object : Migration(1, 3) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // do nothing, because version 2 adds new column and version 3 removes it
        }
    }

    val ALL: Array<Migration> = arrayOf(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_1_3)
}