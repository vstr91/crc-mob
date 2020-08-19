package br.com.vostre.circular;

import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory;
import androidx.room.testing.MigrationTestHelper;

import androidx.test.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import br.com.vostre.circular.model.dao.AppDatabase;

@RunWith(AndroidJUnit4.class)
public class DBTest {

        private static final String TEST_DB = "circular";

        @Rule
        public MigrationTestHelper helper;

        public DBTest() {
            helper = new MigrationTestHelper(InstrumentationRegistry.getInstrumentation(),
                    AppDatabase.class.getCanonicalName(),
                    new FrameworkSQLiteOpenHelperFactory());
        }

//    @Test
//    public void migrate6To7() throws IOException {
//        SupportSQLiteDatabase db = helper.createDatabase(TEST_DB, 6);
//
//        db.close();
//
//        db = helper.runMigrationsAndValidate(TEST_DB, 7, true, AppDatabase.MIGRATION_6_7);
//
//        db.execSQL("SELECT * FROM servico");
//    }
//
//        @Test
//        public void migrate7To8() throws IOException {
//            SupportSQLiteDatabase db = helper.createDatabase(TEST_DB, 7);
//
//            // db has schema version 1. insert some data using SQL queries.
//            // You cannot use DAO classes because they expect the latest schema.
////            db.execSQL(...);
//
//
//
//            // Prepare for the next version.
//            db.close();
//
//            // Re-open the database with version 2 and provide
//            // MIGRATION_1_2 as the migration process.
//            db = helper.runMigrationsAndValidate(TEST_DB, 8, true, AppDatabase.MIGRATION_7_8);
//
//            db.execSQL("SELECT * FROM parametro_interno");
//
//            // MigrationTestHelper automatically verifies the schema changes,
//            // but you need to validate that the data was migrated properly.
//        }
//
//    @Test
//    public void migrate8To9() throws IOException {
//        SupportSQLiteDatabase db = helper.createDatabase(TEST_DB, 8);
//
//        db.close();
//
//        db = helper.runMigrationsAndValidate(TEST_DB, 9, true, AppDatabase.MIGRATION_8_9);
//
//        db.execSQL("SELECT * FROM parametro_interno");
//    }
//
//    @Test
//    public void migrate9To10() throws IOException {
//        SupportSQLiteDatabase db = helper.createDatabase(TEST_DB, 9);
//
//        db.close();
//
//        db = helper.runMigrationsAndValidate(TEST_DB, 10, true, AppDatabase.MIGRATION_9_10);
//
//        db.execSQL("SELECT * FROM parametro_interno");
//    }
//
//    @Test
//    public void migrate6To10() throws IOException {
//        SupportSQLiteDatabase db = helper.createDatabase(TEST_DB, 6);
//
//        db.close();
//
//        db = helper.runMigrationsAndValidate(TEST_DB, 10, true, AppDatabase.MIGRATION_6_10);
//
//        db.execSQL("SELECT * FROM parametro_interno");
//    }

    @Test
    public void migrate6To10Steps() throws IOException {
        SupportSQLiteDatabase db = helper.createDatabase(TEST_DB, 6);

        db.close();

        db = helper.runMigrationsAndValidate(TEST_DB, 10, true, AppDatabase.MIGRATION_6_10);

        db.execSQL("SELECT * FROM parametro_interno");
    }

    @Test
    public void migrate7To10Steps() throws IOException {
        SupportSQLiteDatabase db = helper.createDatabase(TEST_DB, 7);

        db.close();

        db = helper.runMigrationsAndValidate(TEST_DB, 10, true, AppDatabase.MIGRATION_7_10);

        db.execSQL("SELECT * FROM parametro_interno");
    }

    @Test
    public void migrate8To10Steps() throws IOException {
        SupportSQLiteDatabase db = helper.createDatabase(TEST_DB, 8);

        db.close();

        db = helper.runMigrationsAndValidate(TEST_DB, 10, true, AppDatabase.MIGRATION_8_10);

        db.execSQL("SELECT * FROM parametro_interno");
    }

    @Test
    public void migrate9To10Steps() throws IOException {
        SupportSQLiteDatabase db = helper.createDatabase(TEST_DB, 9);

        db.close();

        db = helper.runMigrationsAndValidate(TEST_DB, 10, true, AppDatabase.MIGRATION_9_10);

        db.execSQL("SELECT * FROM parametro_interno");
    }

    @Test
    public void migrate11To12Steps() throws IOException {
        SupportSQLiteDatabase db = helper.createDatabase(TEST_DB, 11);

        db.close();

        db = helper.runMigrationsAndValidate(TEST_DB, 12, true, AppDatabase.MIGRATION_11_12);

        db.execSQL("SELECT * FROM parametro_interno");
    }

}
