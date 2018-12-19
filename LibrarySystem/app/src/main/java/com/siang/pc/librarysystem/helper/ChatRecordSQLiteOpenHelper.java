package com.siang.pc.librarysystem.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.siang.pc.librarysystem.entity.ChatMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by siang on 2018/5/31.
 * 这个类是用于操作保存聊天记录的SQLite的辅助器
 */

public class ChatRecordSQLiteOpenHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "Chat";
    private static final int DB_VERSION = 1;
    private static final String TABLE_NAME = "MessageRecord";
    private static final String COL_USERNAME = "username";
    private static final String COL_CONTENT = "content";
    private static final String COL_TYPE = "type";

    private static final String TABLE_CREAT =
            "CREATE TABLE " + TABLE_NAME + " ( " +
                    COL_USERNAME + " TEXT, " +
                    COL_CONTENT + " TEXT, " +
                    COL_TYPE + " INTEGER ); ";

    public ChatRecordSQLiteOpenHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREAT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
    }

    public List<ChatMessage> getAllChatMessage() {
        SQLiteDatabase db = getReadableDatabase();
        String[] columns = {
                COL_USERNAME, COL_CONTENT, COL_TYPE
        };
        Cursor cursor = db.query(TABLE_NAME, columns, null, null, null, null, null);
        List<ChatMessage> chatMessageList = new ArrayList<>();
        while (cursor.moveToNext()) {
            String username = cursor.getString(0);
            String content = cursor.getString(1);
            int type = cursor.getInt(2);
            ChatMessage chatMessage = new ChatMessage(username, content, type);
            chatMessageList.add(chatMessage);
        }
        cursor.close();
        return  chatMessageList;
    }

    public long insertChatMessage(ChatMessage chatMessage) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USERNAME, chatMessage.getUsername());
        values.put(COL_CONTENT, chatMessage.getContent());
        values.put(COL_TYPE, chatMessage.getType());
        return db.insert(TABLE_NAME, null, values);
    }

    public void deleteAllChatMessage() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME);
    }
}
