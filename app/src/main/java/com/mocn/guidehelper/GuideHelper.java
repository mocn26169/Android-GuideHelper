package com.mocn.guidehelper;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.List;


public class GuideHelper {

    private  List<PageData> pageDatas;
    private Activity activity;
    private Dialog guideDialog;
    private RelativeLayout guidelayout;

    public GuideHelper(Activity activity, List<PageData> pageDatas) {
        this.activity = activity;
        this.pageDatas = pageDatas;
    }

    public void show() {
        if (pageDatas.size() <= 0) {
            Log.e("GuideHelper", "没有数据");
            return;
        }
        if (guidelayout == null) {
            guidelayout = new RelativeLayout(activity);

            //创建Dialog，不遮挡状态栏
            guideDialog = new Dialog(activity, R.style.popupDialog);
            guideDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            guideDialog.setContentView(guidelayout);
            guideDialog.setCanceledOnTouchOutside(false);
            guideDialog.setCancelable(false);
            guideDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0x66000000));
            //设置不遮挡状态栏
            WindowManager.LayoutParams lay = guideDialog.getWindow().getAttributes();
            DisplayMetrics dm = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
            Rect rect = new Rect();
            View view = activity.getWindow().getDecorView();//decorView是window中的最顶层view，可以从window中获取到decorView
            view.getWindowVisibleDisplayFrame(rect);
            lay.height = dm.heightPixels - rect.top;
            lay.width = dm.widthPixels;
            guideDialog.show();

//            //创建Dialog，遮挡状态栏
//            guideDialog = new Dialog(activity, android.R.style.Theme_DeviceDefault_Light_DialogWhenLarge_NoActionBar);
//            //设置背景颜色
//            guideDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0x66000000));
//            //设置自定义的布局
//            guideDialog.setContentView(guidelayout);
//            //设置布局的属性
//            guideDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
//           //设置点击不能取消
//            guideDialog.setCancelable(false);
//            //显示Dialog
//            guideDialog.show();
        }

        pageDatas.get(0).getLightView().post(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void run() {
                showTip(guidelayout);
            }
        });

    }

    private void showTip(final RelativeLayout guidelayout) {
        View lightView = pageDatas.get(0).getLightView();
        int tipsImageResourceId = pageDatas.get(0).getTipsImageResourceId();

        /**********显示高亮控件**********/
        //获取view的宽高
        int vWidth = lightView.getMeasuredWidth();
        int vHeight = lightView.getMeasuredHeight();

        //如果宽高都小于等于0，再measure试下获取
        if (vWidth <= 0 || vHeight <= 0) {
            ViewGroup.LayoutParams mlayoutParams = lightView.getLayoutParams();
            lightView.measure(mlayoutParams.width, mlayoutParams.height);
            vWidth = lightView.getMeasuredWidth();
            vHeight = lightView.getMeasuredHeight();
        }

        //获取不到宽高则返回操作
        if (vWidth <= 0 || vHeight <= 0) {
            Log.e("GuideHelper", "宽高都小于等于0");
            return;
        }

        //获取view在屏幕的位置
        int[] location = new int[2];
        lightView.getLocationOnScreen(location);

        //获取layout在屏幕上的位置
        int layoutOffset[] = new int[2];
        guidelayout.getLocationOnScreen(layoutOffset);

        //这里避免dialog不是全屏，导致view的绘制位置不对应
        location[1] -= layoutOffset[1];

        //开启能缓存图片信息
        lightView.setDrawingCacheEnabled(true);
        //获取视图缓存
        lightView.buildDrawingCache();

        Bitmap LightBitmap = lightView.getDrawingCache();
        if (LightBitmap != null) {
            //根据缓存获取Bitmap
            LightBitmap = Bitmap.createBitmap(LightBitmap);
        } else {
            //如果获取不到，则用创建一个view宽高一样的bitmap用canvas把view绘制上去
            LightBitmap = Bitmap.createBitmap(vWidth, vHeight, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(LightBitmap);
            lightView.draw(canvas);
        }

        //关闭能缓存图片信息
        lightView.setDrawingCacheEnabled(false);
        //释放缓存
        lightView.destroyDrawingCache();

        //设置ImageView属性
        ImageView newLightView = new ImageView(activity);
        newLightView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        newLightView.setImageBitmap(LightBitmap);

        //动态设置Viwe的id
        int  imageViewId = R.id.snack;
        newLightView.setId(imageViewId);

        //设置位置
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.leftMargin = location[0];
        params.topMargin = location[1];

        //添加到布局
        guidelayout.addView(newLightView, params);

        /**********显示提示图片**********/
        //获取提示图片的Bitmap
        Bitmap tipBitmap = BitmapFactory.decodeResource(activity.getResources(), tipsImageResourceId);

        //设置大小
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int showViewHeight = tipBitmap.getHeight();
        int showViewWidth = tipBitmap.getWidth();

        //设置ImageView属性
        ImageView newTipView = new ImageView(activity);
        newTipView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        layoutParams.width = showViewWidth;
        layoutParams.height = showViewHeight;
        newTipView.setImageBitmap(tipBitmap);

        //设置间距(可自行封装)
        //layoutParams.topMargin += dipToPix(activity, 20);

        //设置相对位置(可自行封装)
        layoutParams.addRule(RelativeLayout.BELOW, newLightView.getId());
        layoutParams.addRule(RelativeLayout.ALIGN_LEFT, newLightView.getId());
        //layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);

        //添加到布局
        guidelayout.addView(newTipView, layoutParams);

        //点击布局执行下一步动作
        guidelayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //清理布局控件
                guidelayout.removeAllViews();
                //操作有第二步继续执行
                if (pageDatas.size() >= 2) {
                    pageDatas.remove(0);
                    show();
                } else {
                    guideDialog.dismiss();
                }
            }
        });

    }

    public static class PageData {
        /**
         * 高亮View
         */
        private View lightView;

        /**
         * 提示图片id
         */
        private int tipsImageResourceId;

        public PageData(View lightView, int tipsImageResourceId) {
            this.lightView = lightView;
            this.tipsImageResourceId = tipsImageResourceId;
        }

        public View getLightView() {
            return lightView;
        }

        public void setLightView(View lightView) {
            this.lightView = lightView;
        }

        public int getTipsImageResourceId() {
            return tipsImageResourceId;
        }

        public void setTipsImageResourceId(int tipsImageResourceId) {
            this.tipsImageResourceId = tipsImageResourceId;
        }

    }

    /**
     * dip转px
     */
    public int dipToPix(Context context, int dip) {
        int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, context.getResources().getDisplayMetrics());
        return size;
    }

    /**
     * 实例化布局
     *
     * @param layoutId
     * @return
     */
    public View inflate(int layoutId) {
        LayoutInflater inflater = LayoutInflater.from(activity);
        View view = inflater.inflate(layoutId, null);
        return view;
    }

}
