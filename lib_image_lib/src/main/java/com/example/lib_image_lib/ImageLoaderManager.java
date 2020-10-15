package com.example.lib_image_lib;

import android.app.Notification;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.NotificationTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.example.lib_image_lib.utils.CustomRequestListener;
import com.example.lib_image_lib.utils.Utils;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import static com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions.withCrossFade;

/**
 * 图处加载类，外界唯一调用类,直持为view,notifaication,appwidget加载图片
 * Glide 支持拉取，解码和展示视频快照，图片，和GIF动画。
 */
public class ImageLoaderManager {

    private ImageLoaderManager() {
    }

    public static ImageLoaderManager getInstance() {
        return ImageLoaderManager.SingletonHolder.instance;
    }

    private static class SingletonHolder {
        private static ImageLoaderManager instance = new ImageLoaderManager();
    }

    /**
     * 为notification加载图
     */
    public void displayImageForNotification(Context context, RemoteViews rv, int id,
                                            Notification notification, int NOTIFICATION_ID, String url) {
        this.displayImageForTarget(context,
                initNotificationTarget(context, id, rv, notification, NOTIFICATION_ID), url);
    }

    /**
     * 不带回调的加载
     */
    public void displayImageForView(ImageView imageView, String url) {
        this.displayImageForView(imageView, url, null);
    }

    /**
     * 带回调的加载图片方法
     */
    public void displayImageForView(ImageView imageView, String url,
                                    CustomRequestListener requestListener) {
        Glide.with(imageView.getContext())
                .asBitmap()
                .load(url)
                .apply(initCommonRequestOption())
                .transition(withCrossFade())
                .into(imageView);
    }

    /**
     * 带回调的加载图片方法
     */
    public void displayImageForCircle(final ImageView imageView, String url) {
        Glide.with(imageView.getContext())
                .asBitmap()
                .load(url)
                .apply(initCommonRequestOption())
                .into(new BitmapImageViewTarget(imageView) {
                    @Override
                    protected void setResource(final Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(imageView.getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        imageView.setImageDrawable(circularBitmapDrawable);
                    }
                });
    }

//    public void displayImageForViewGroup(final ViewGroup group, String url) {
//        Glide.with(group.getContext())
//                .asBitmap()
//                .load(url)
//                .apply(initCommonRequestOption())
//                .into(new SimpleTarget<Bitmap>() {//设置宽高
//                    @Override
//                    public void onResourceReady(@NonNull Bitmap resource,
//                                                @Nullable Transition<? super Bitmap> transition) {
//                        final Bitmap res = resource;
//                        Observable.just(resource)
//                                .map(new Function<Bitmap, Drawable>() {
//                                    @Override
//                                    public Drawable apply(Bitmap bitmap) {
//                                        Drawable drawable = new BitmapDrawable(
//                                                Utils.doBlur(res, 100, true)
//                                        );
//                                        return drawable;
//                                    }
//                                })
//                                .subscribeOn(Schedulers.io())
//                                .observeOn(AndroidSchedulers.mainThread())
//                                .subscribe(new Consumer<Drawable>() {
//                                    @Override
//                                    public void accept(Drawable drawable) throws Exception {
//                                        group.setBackground(drawable);
//                                    }
//                                });
//                    }
//                });
//    }

    /**
     * 为非view加载图片
     */
    private void displayImageForTarget(Context context, Target target, String url) {
        this.displayImageForTarget(context, target, url, null);
    }

    /**
     * 为非view加载图片
     */
    private void displayImageForTarget(Context context, Target target, String url,
                                       CustomRequestListener requestListener) {
        Glide.with(context)
                .asBitmap()
                .load(url)
                .apply(initCommonRequestOption())
                .transition(withCrossFade())
                .fitCenter()
                .listener(requestListener)
                .into(target);
    }

    /*
     * 初始化Notification Target
     */
    private NotificationTarget initNotificationTarget(Context context, int id, RemoteViews rv,
                                                      Notification notification, int NOTIFICATION_ID) {
        NotificationTarget notificationTarget =
                new NotificationTarget(context, id, rv, notification, NOTIFICATION_ID);
        return notificationTarget;
    }

    private RequestOptions initCommonRequestOption() {
        RequestOptions options = new RequestOptions();
        options.placeholder(R.drawable.default_error)//占位符
                .error(R.drawable.default_error)//错误符
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)//磁盘缓存策略（Disk Cache Strategy）
                .skipMemoryCache(false)//不跳过缓存，就是使用缓存
                .priority(Priority.NORMAL);
        return options;
    }

    /**
     * 缓存单个网络图片到本地或者内存中
     */
    public void cachePic(final Context context,final String url){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (!TextUtils.isEmpty(url)){
                        Glide.with(context).downloadOnly().load(url).submit().get();
                    }
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 缓存多个网络图片到本地或者内存中
     */
    public void cachePicList(final Context context,final ArrayList<String> urlList){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for (int i = 0; i < urlList.size(); i++) {
                        String url = urlList.get(i);
                        Glide.with(context).downloadOnly().load(url).submit().get();
                    }
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
