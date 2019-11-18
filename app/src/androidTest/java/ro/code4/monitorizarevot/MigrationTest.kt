package ro.code4.monitorizarevot

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import androidx.room.Room
import androidx.room.testing.MigrationTestHelper
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import ro.code4.monitorizarevot.data.AppDatabase
import ro.code4.monitorizarevot.data.Migrations
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class MigrationTest {

    @get:Rule
    val helper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        AppDatabase::class.java.canonicalName
    )

    @Test
    @Throws(IOException::class)
    fun migrate1To2() {
        var rowId = -1L
        helper.createDatabase(TEST_DB, 1).use {
            rowId = it.insert(
                "county",
                SQLiteDatabase.CONFLICT_REPLACE,
                ContentValues().apply {
                    put("id", 1)
                    put("code", "AA")
                    put("name", "TEST")
                    put("`limit`", 12)
                }
            )
        }

        helper.runMigrationsAndValidate(TEST_DB, 2, true, Migrations.MIGRATION_1_2).use { db ->
            db.query("SELECT * FROM county WHERE rowid = ?", arrayOf(rowId)).use { c ->
                assertNotNull(c)
                assertTrue(c.moveToFirst())
                // Check new column
                assertEquals(null, c.getInt(c.getColumnIndex("diaspora")))
                // Check some old columns
                assertEquals(1, c.getInt(c.getColumnIndex("id")))
                assertEquals("AA", c.getString(c.getColumnIndex("code")))
            }
        }

    }

    @Test
    @Throws(IOException::class)
    fun migrateAll() {
        // Create earliest version of the database.
        helper.createDatabase(TEST_DB, 1).apply {
            close()
        }

        // Open latest version of the database. Room will validate the schema
        // once all migrations execute.
        Room.databaseBuilder(
            InstrumentationRegistry.getInstrumentation().targetContext,
            AppDatabase::class.java,
            TEST_DB
        ).addMigrations(*Migrations.ALL).build().apply {
            openHelper.writableDatabase
            close()
        }
    }


    companion object {
        private const val TEST_DB = "test-db"
    }
}