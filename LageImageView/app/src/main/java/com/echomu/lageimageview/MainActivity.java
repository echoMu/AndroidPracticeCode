package com.echomu.lageimageview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.echomu.lageimageview.view.LargeImageView;

import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
//    private ImageView ivLagerImage;
    private LargeImageView ivLagerImage2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        ivLagerImage=findViewById(R.id.iv_lager_image);
        ivLagerImage2=(LargeImageView) findViewById(R.id.iv_lager_image2);

        InputStream inputStream = null;
        try {
            inputStream = getAssets().open("qm.jpg");
            ivLagerImage2.setInputStream(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }


//        try {
//            InputStream inputStream=getAssets().open("qm.jpg");
//            BitmapFactory.Options options=new BitmapFactory.Options();
//            options.inJustDecodeBounds=true;
//            //获得图片的宽、高
//            BitmapFactory.decodeStream(inputStream,null,options);
//            int originWith=options.outWidth;
//            int originHeight=options.outHeight;
//
//
//            //设置显示图片的中心区域
//            BitmapRegionDecoder bitmapRegionDecoder=BitmapRegionDecoder.newInstance(inputStream,false);
//            BitmapFactory.Options centerOptions=new BitmapFactory.Options();
//            centerOptions.inPreferredConfig= Bitmap.Config.RGB_565;
//            Bitmap bitmap=bitmapRegionDecoder.decodeRegion(new Rect(originWith/2-500,originHeight/2-500,
//                    originWith/2+500,originHeight/2+500),centerOptions);
//            ivLagerImage.setImageBitmap(bitmap);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
}
