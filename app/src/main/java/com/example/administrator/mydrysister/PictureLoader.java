package com.example.administrator.mydrysister;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by Administrator on 2018-01-31 0031.
 */
public class PictureLoader {
    private ImageView loadimg;
    private String imgUrl;
    private byte[] picByte;
    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {


            if (msg.what == 0x123) {
                if (picByte != null) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(picByte, 0, picByte.length);
                    loadimg.setImageBitmap(bitmap);
                }
            }
            return false;
        }
    });

    public void load(ImageView loadimg, String imgUrl) {
        this.loadimg = loadimg;
        this.imgUrl = imgUrl;
        Drawable drawable = loadimg.getDrawable();
        if (drawable != null && drawable instanceof BitmapDrawable) {
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }
        }
        new Thread(runnable).start();
    }

    Runnable runnable=new Runnable() {
        @Override
        public void run() {
            try {
                URL url=new URL(imgUrl);

                HttpURLConnection conn= (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setReadTimeout(10000);
                if (conn.getResponseCode() == 200) {
                    InputStream in = conn.getInputStream();
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    byte[] bytes = new byte[1024];
                    int length = -1;
                    while ((length = in.read(bytes)) != -1) {
                        out.write(bytes, 0, length);
                    }
                    picByte = out.toByteArray();
                    in.close();
                    out.close();
                    handler.sendEmptyMessage(0x123);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };
}
