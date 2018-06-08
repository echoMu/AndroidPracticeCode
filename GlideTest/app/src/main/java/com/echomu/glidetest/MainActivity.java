package com.echomu.glidetest;

import android.app.ProgressDialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.bumptech.glide.request.target.Target;

public class MainActivity extends AppCompatActivity {
    ImageView imageView;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView=findViewById(R.id.iv_main);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("加载中");
    }

    public void loadImage(View view) {
//        String url="http://tse2.mm.bing.net/th?id=OIP.2CKbr09GVD2Gn-ooqHbO6AHaEo&pid=15.1";
        final String url="http://img1.cfw.cn/editors/attached/image/20170303/20170303105634743474.gif";
//        Glide.with(this).load(url).placeholder(R.mipmap.ic_launcher_round).into(imageView);

        ProgressInterceptor.addListener(url, new ProgressListener() {
            @Override
            public void onProgress(int progress) {
                progressDialog.setProgress(progress);
            }
        });

        Glide.with(this)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                .into(new GlideDrawableImageViewTarget(imageView){
                    @Override
                    public void onLoadStarted(Drawable placeholder) {
                        super.onLoadStarted(placeholder);

                        progressDialog.show();
                    }

                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
                        super.onResourceReady(resource, animation);

                        progressDialog.dismiss();
                        ProgressInterceptor.removeListener(url);
                    }
                });
    }

}
