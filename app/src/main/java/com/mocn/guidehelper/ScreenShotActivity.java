package com.mocn.guidehelper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.R.attr.bitmap;

public class ScreenShotActivity extends AppCompatActivity {
    private Button btn_save;
    private ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_shot);
        btn_save = (Button) findViewById(R.id.btn_save);
        scrollView = (ScrollView) findViewById(R.id.scrollView);

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                saveView();
                saveLongView();
            }
        });
    }

    /**
     * 截取当前程序界面
     */
    private void saveView() {
        //DecorView只有一个子元素为LinearLayout。代表整个Window界面，包含通知栏，标题栏，内容显示栏三块区域
        View decorView = getWindow().getDecorView();
        //开启能缓存图片信息
        decorView.setDrawingCacheEnabled(true);
        //获取视图缓存
        decorView.buildDrawingCache();
        //根据缓存获取Bitmap
        Bitmap bmp = decorView.getDrawingCache();

        Rect rect = new Rect();
        //getWindowVisibleDisplayFrame方法可以获取到程序显示的区域，包括标题栏，但不包括状态栏
        decorView.getWindowVisibleDisplayFrame(rect);
        //获取状态栏高度
        int statusBarHeight = rect.top;

        //获取图片宽高
        int width = bmp.getWidth();
        int height = bmp.getHeight();

        //坐标轴和高度都减去状态栏的高度
        Bitmap saveBmp = Bitmap.createBitmap(bmp, 0, statusBarHeight,
                width, height - statusBarHeight, null, false);

        //关闭能缓存图片信息
        decorView.setDrawingCacheEnabled(false);
        //释放缓存
        decorView.destroyDrawingCache();

        //将图片保存到SD卡
        saveBitmap("ScreenShot", saveBmp);
    }

    /**
     * 截取超过程序界面的长图
     */
    private void saveLongView() {
        int h = 0;
        // 获取listView实际高度
//        for (int i = 0; i < listView.getChildCount(); i++) {
//            h += listView.getChildAt(i).getHeight();
//        }
        // 获取scrollView实际高度
        for (int i = 0; i < scrollView.getChildCount(); i++) {
            h += scrollView.getChildAt(i).getHeight();
        }
        Log.i("ScreenShot", " 高度:" + scrollView.getHeight());
        Log.i("ScreenShot", "实际高度:" + h);

        Bitmap bitmap;
        // 创建对应大小的bitmap
        bitmap = Bitmap.createBitmap(scrollView.getWidth(), h,
                Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmap);
        scrollView.draw(canvas);

        //将图片保存到SD卡
        saveBitmap("ScreenShot", bitmap);
    }

    /**
     * 保存图片
     *
     * @param bitName
     * @param mBitmap
     */
    public void saveBitmap(String bitName, Bitmap mBitmap) {
        //创建目录
        String path = createSDCardDirectory(ScreenShotActivity.this, "Android-GuideHelper", "Android-GuideHelper-Image");
        //路径
        String filePath = path + "/" + bitName + ".png";
        File f = new File(filePath);
        try {
            f.createNewFile();
        } catch (IOException e) {
            Log.e("ScreenShotActivity", "保存图片时出错：" + e.toString());
        }
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建SDCard目录
     */
    public String createSDCardDirectory(Context context, String appName, String position) {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            // 创建一个文件夹对象，赋值为外部存储器的目录
            File sdcardDir = Environment.getExternalStorageDirectory();

            //得到一个路径，内容是sdcard的文件夹路径和名字
//            String path = sdcardDir.getPath() + "/" + appName + "/" + position;
            String path = sdcardDir.getPath() + "/" + position;
            File filePath = new File(path);
            if (!filePath.exists()) {
                //若不存在，创建目录，可以在应用启动的时候创建
                boolean isSuccess = filePath.mkdirs();
                Log.e("ScreenShotActivity", "创建文件夹路径是否成功=" + isSuccess);
            }
            return path;
        } else {
            Log.e("ScreenShotActivity", "创建保存路径失败，无法找到SDCard");
            return null;
        }
    }

    /**
     * 创建缓存目录
     */
    public String createCacheDirectory(Context context, String appName, String position) {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            // 创建一个文件夹对象，赋值为外部存储器的目录
            File sdcardDir = context.getExternalFilesDir(position);

            //得到一个路径，内容是sdcard的文件夹路径和名字
            String path = sdcardDir.getPath() + "/" + position;

            File filePath = new File(path);
            if (!filePath.exists()) {
                //若不存在，创建目录，可以在应用启动的时候创建
                boolean isSuccess = filePath.mkdirs();
                Log.e("ScreenShotActivity", "创建文件夹路径是否成功=" + isSuccess);
            }
            return path;
        } else {
            Log.e("ScreenShotActivity", "创建保存路径失败，无法找到SDCard");
            return null;
        }
    }
}
