package ro.code4.monitorizarevot

import android.content.ContentValues
import android.database.Cursor.FIELD_TYPE_NULL
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
import java.util.*

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
        helper.createDatabase(TEST_DB, 1).use {
            val rowId = it.insert(
                "county",
                SQLiteDatabase.CONFLICT_FAIL,
                ContentValues().apply {
                    put("id", 1)
                    put("code", "AA")
                    put("name", "TEST")
                    put("`limit`", 12)
                }
            )
            assertTrue(rowId > 0)
        }
        helper.runMigrationsAndValidate(TEST_DB, 2, true, Migrations.MIGRATION_1_2).use { db ->
            val foundDataCursor = db.query("SELECT * FROM county")
            assertNotNull(foundDataCursor)
            foundDataCursor.use {
                assertNotNull(it)
                // we have a single row, previously inserted
                assertEquals(1, it.count)
                assertTrue(it.moveToFirst())
                // at this moment we expect 5 columns for the county table
                assertEquals(5, it.columnCount)
                // check for the diaspora column and that it has null as value(immediately after the
                // migration), simply using getInt() doesn't work due to the undefined way the method
                // behaves when having null in an INTEGER field
                assertEquals(FIELD_TYPE_NULL, it.getType(it.getColumnIndex("diaspora")))
                // check for older columns
                assertEquals(1, it.getInt(it.getColumnIndex("id")))
                assertEquals("AA", it.getString(it.getColumnIndex("code")))
                assertEquals("TEST", it.getString(it.getColumnIndex("name")))
                assertEquals(12, it.getInt(it.getColumnIndex("limit")))
            }
        }
    }

    @Test
    fun migrate2To3() {
        helper.createDatabase(TEST_DB, 2).use {
            val values = ContentValues().apply {
                put("id", 1)
                put("code", "AA")
                put("name", "TEST")
                put("`limit`", 12)
                put("diaspora", false)
            }
            val rowId = it.insert("county", SQLiteDatabase.CONFLICT_FAIL, values)
            assertTrue(rowId > 0)
        }
        val db = helper.runMigrationsAndValidate(TEST_DB, 3, true, Migrations.MIGRATION_2_3)
        val foundDataCursor = db.query("SELECT * FROM county")
        assertNotNull(foundDataCursor)
        foundDataCursor.use {
            assertNotNull(it)
            // we have a single row, previously inserted
            assertEquals(1, it.count)
            assertTrue(it.moveToFirst())
            // at this point we expect to have exactly 6 columns for the county table
            assertEquals(6, it.columnCount)
            // check for the new column "order" and that it has the default value of 0
            assertEquals(0, it.getInt(it.getColumnIndex("order")))
            // check for older columns
            assertEquals(1, it.getInt(it.getColumnIndex("id")))
            assertEquals("AA", it.getString(it.getColumnIndex("code")))
            assertEquals("TEST", it.getString(it.getColumnIndex("name")))
            val diasporaColumnValue = it.getInt(it.getColumnIndex("diaspora")) != 0
            assertEquals(false, diasporaColumnValue)
        }
    }

    @Test
    fun migrate3To4() {
        check3To4MigrationFor(ContentValues().apply {
            put("uniqueId", "unique_section")
            put("formId", 100)
        }, "section", 5, "SELECT * FROM section")
        check3To4MigrationFor(ContentValues().apply {
            put("id", 100)
            put("text", "question_text")
            put("code", "question_code")
            put("questionType", 0)
            put("sectionId", "section_id")
            put("hasNotes", false)
        }, "question", 7, "SELECT * FROM question")
        check3To4MigrationFor(ContentValues().apply {
            put("idOption", 100)
            put("text", "answer_text")
            put("isFreeText", false)
            put("questionId", 0)
        }, "answer", 5, "SELECT * FROM answer")
    }

    private fun check3To4MigrationFor(
        testValues: ContentValues,
        tableName: String,
        nrOfColumns: Int,
        query: String
    ) {
        helper.createDatabase(TEST_DB, 3).use {
            val rowId = it.insert(tableName, SQLiteDatabase.CONFLICT_FAIL, testValues)
            assertTrue(rowId > 0)
        }
        val db = helper.runMigrationsAndValidate(TEST_DB, 4, true, Migrations.MIGRATION_3_4)
        val sectionsCursor = db.query(query)
        assertNotNull(sectionsCursor)
        sectionsCursor.use {
            // we have a single row, previously inserted
            assertEquals(1, it.count)
            assertTrue(it.moveToFirst())
            assertEquals(nrOfColumns, it.columnCount)
            // check for the new column "orderNumber" and that it has the default value of 0
            assertEquals(0, it.getInt(it.getColumnIndex("orderNumber")))
        }
    }

    @Test
    fun migrate4To5() {
        val expectedTime = Date().time
        helper.createDatabase(TEST_DB, 4).use {
            val values = ContentValues().apply {
                put("id", 1)
                put("uriPath", "/fake/path/on/disk/for/file/image/jpg")
                put("description", "description for note")
                put("questionId", 12)
                put("date", expectedTime)
                put("countyCode", "B")
                put("pollingStationNumber", 55)
                put("synced", false)
            }
            val rowId = it.insert("note", SQLiteDatabase.CONFLICT_FAIL, values)
            assertTrue(rowId > 0)
        }
        val db = helper.runMigrationsAndValidate(TEST_DB, 5, true, Migrations.MIGRATION_4_5)
        val noteDataCursor = db.query("SELECT * FROM note")
        assertNotNull(noteDataCursor)
        noteDataCursor.use {
            // we have a single row, previously inserted
            assertEquals(1, it.count)
            assertTrue(it.moveToFirst())
            // at this point we expect to have exactly 10 columns for the note table
            assertEquals(10, it.columnCount)
            // check for the new column "formCode" and that it has the default value of null
            assertNull(it.getString(it.getColumnIndex("formCode")))
            // check for the new column "questionCode" and that it has the default value of null
            assertNull(it.getString(it.getColumnIndex("questionCode")))
            // check for older columns
            assertEquals(1, it.getInt(it.getColumnIndex("id")))
            assertEquals(
                "/fake/path/on/disk/for/file/image/jpg",
                it.getString(it.getColumnIndex("uriPath"))
            )
            assertEquals("description for note", it.getString(it.getColumnIndex("description")))
            assertEquals(12, it.getInt(it.getColumnIndex("questionId")))
            assertEquals(expectedTime, it.getLong(it.getColumnIndex("date")))
            assertEquals(55, it.getInt(it.getColumnIndex("pollingStationNumber")))
            assertEquals("B", it.getString(it.getColumnIndex("countyCode")))
            assertTrue(it.getInt(it.getColumnIndex("synced")) == 0)
        }
    }

    @Test
    @Throws(IOException::class)
    fun migrateAll() {
        // Create earliest version of the database.
        helper.createDatabase(TEST_DB, 1).apply {
            close()
        }
        // Open latest version of the database in memory. Room will validate the schema
        // once all migrations execute.
        Room.inMemoryDatabaseBuilder(
            InstrumentationRegistry.getInstrumentation().targetContext,
            AppDatabase::class.java
        ).addMigrations(*Migrations.ALL).build().apply {
            openHelper.writableDatabase
            close()
        }
    }

    companion object {
        private const val TEST_DB = "test-db"
    }
}