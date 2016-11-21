package com.and2long.musicplayer.utils;

import com.and2long.musicplayer.bean.SongBean;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by L on 2016/11/2.
 */

public class FileUtils {

    public static void writeObjectToFile(List<SongBean> list, String path) {
        File file = new File(path);
        FileOutputStream out;
        try {
            out = new FileOutputStream(file);
            ObjectOutputStream objOut = new ObjectOutputStream(out);
            objOut.writeObject(list);
            objOut.flush();
            objOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<SongBean> readObjectFromFile(String path) {
        File file = new File(path);
        FileInputStream in;
        ArrayList<SongBean> object = null;
        try {
            in = new FileInputStream(file);
            ObjectInputStream objIn = new ObjectInputStream(in);
            object = (ArrayList<SongBean>) objIn.readObject();
            objIn.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return object;
    }
}
