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
import java.util.Date

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
                "note",
                SQLiteDatabase.CONFLICT_REPLACE,
                ContentValues().apply {
                    put("id", 1)
                    put("description", "test description")
                    put("date", Date().time)
                    put("countyCode", "TEST")
                    put("branchNumber", 1)
                    put("synced", 1)
                }
            )
        }

        helper.runMigrationsAndValidate(TEST_DB, 2, true, Migrations.MIGRATION_1_2).use { db ->
            db.query("SELECT * FROM note WHERE rowid = ?", arrayOf(rowId)).use { c ->
                assertNotNull(c)
                assertTrue(c.moveToFirst())
                // Check new column
                assertEquals(-1, c.getInt(c.getColumnIndex("someNewField")))
                // Check some old columns
                assertEquals(1, c.getInt(c.getColumnIndex("id")))
                assertEquals("test description", c.getString(c.getColumnIndex("description")))
            }
        }

    }

    @Test
    @Throws(IOException::class)
    fun migrate2To3() {
        var rowId = -1L
        helper.createDatabase(TEST_DB, 2).use {
            rowId = it.insert(
                "note",
                SQLiteDatabase.CONFLICT_REPLACE,
                ContentValues().apply {
                    put("id", 1)
                    put("description", "test description")
                    put("date", Date().time)
                    put("countyCode", "TEST")
                    put("branchNumber", 1)
                    put("synced", 1)
                    put("someNewField", 2)
                }
            )
        }

        helper.runMigrationsAndValidate(TEST_DB, 3, true, Migrations.MIGRATION_2_3).use { db ->
            db.query("SELECT * FROM note WHERE rowid = ?", arrayOf(rowId)).use { c ->
                assertNotNull(c)
                assertTrue(c.moveToFirst())
                // Check column removed
                assertEquals(-1, c.getColumnIndex("someNewField"))
                // Check some old columns
                assertEquals(1, c.getInt(c.getColumnIndex("id")))
                assertEquals("test description", c.getString(c.getColumnIndex("description")))
            }
        }
    }

    @Test
    @Throws(IOException::class)
    fun migrate1To3() {
        var rowId = -1L
        helper.createDatabase(TEST_DB, 1).use {
            rowId = it.insert(
                "note",
                SQLiteDatabase.CONFLICT_REPLACE,
                ContentValues().apply {
                    put("id", 1)
                    put("description", "test description")
                    put("date", Date().time)
                    put("countyCode", "TEST")
                    put("branchNumber", 1)
                    put("synced", 1)
                }
            )
        }

        helper.runMigrationsAndValidate(TEST_DB, 3, true, Migrations.MIGRATION_1_3).use { db ->
            db.query("SELECT * FROM note WHERE rowid = ?", arrayOf(rowId)).use { c ->
                assertNotNull(c)
                assertTrue(c.moveToFirst())
                // Check column removed
                assertEquals(-1, c.getColumnIndex("someNewField"))
                // Check some old columns
                assertEquals(1, c.getInt(c.getColumnIndex("id")))
                assertEquals("test description", c.getString(c.getColumnIndex("description")))
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