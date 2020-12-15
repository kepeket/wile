package com.wile.database.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Schema {
    companion object {
        val MIGRATION_4_5: Migration = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE IF NOT EXISTS `Reminder`" +
                        "(`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                        "`workout` INTEGER NOT NULL," +
                        "`active` INTEGER NOT NULL," +
                        "`time` TEXT NOT NULL," +
                        "`monday` INTEGER NOT NULL," +
                        "`tuesday` INTEGER NOT NULL," +
                        "`wednesday` INTEGER NOT NULL," +
                        "`thursday` INTEGER NOT NULL," +
                        "`friday` INTEGER NOT NULL," +
                        "`saturday` INTEGER NOT NULL," +
                        "`sunday` INTEGER NOT NULL)")
            }
        }

        val MIGRATION_5_6: Migration = object : Migration(5, 6) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE `Reminder`" +
                        "ADD COLUMN `hour` INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE `Reminder`" +
                        "ADD COLUMN `minute` INTEGER NOT NULL DEFAULT 0")
            }
        }

        val MIGRATION_6_7: Migration = object : Migration(6, 7) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("DROP TABLE `Reminder`")
                database.execSQL("CREATE TABLE IF NOT EXISTS `Reminder`" +
                        "(`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                        "`workout` INTEGER NOT NULL," +
                        "`active` INTEGER NOT NULL," +
                        "`hour` INTEGER NOT NULL," +
                        "`minute` INTEGER NOT NULL," +
                        "`monday` INTEGER NOT NULL," +
                        "`tuesday` INTEGER NOT NULL," +
                        "`wednesday` INTEGER NOT NULL," +
                        "`thursday` INTEGER NOT NULL," +
                        "`friday` INTEGER NOT NULL," +
                        "`saturday` INTEGER NOT NULL," +
                        "`sunday` INTEGER NOT NULL)")
            }
        }
    }
}