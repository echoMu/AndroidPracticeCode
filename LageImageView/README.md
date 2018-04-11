# Android 高清加载巨图方案 拒绝压缩图片
原文地址是鸿洋大神的博客文章(https://blog.csdn.net/lmj623565791/article/details/49300989/)

对于加载图片，大家都不陌生，一般为了尽可能避免OOM都会按照如下做法：

对于图片显示：根据需要显示图片控件的大小对图片进行压缩显示。
如果图片数量非常多：则会使用LruCache等缓存机制，将所有图片占据的内容维持在一个范围内。
其实对于图片加载还有种情况，就是单个图片非常巨大，并且还不允许压缩。比如显示：世界地图、清明上河图、微博长图等。

那么对于这种需求，该如何做呢？

首先不压缩，按照原图尺寸加载，那么屏幕肯定是不够大的，并且考虑到内存的情况，不可能一次性整图加载到内存中，所以肯定是局部加载，那么就需要用到一个类：

> BitmapRegionDecoder

其次，既然屏幕显示不完，那么最起码要添加一个上下左右拖动的手势，让用户可以拖动查看。

BitmapRegionDecoder主要用于显示图片的某一块矩形区域，如果你需要显示某个图片的指定区域，那么这个类非常合适。

对于该类的用法，非常简单，既然是显示图片的某一块区域，那么至少只需要一个方法去设置图片；一个方法传入显示的区域即可；详见：

· BitmapRegionDecoder提供了一系列的newInstance方法来构造对象，支持传入文件路径，文件描述符，文件的inputstrem等。

例如：

    BitmapRegionDecoder bitmapRegionDecoder =
    BitmapRegionDecoder.newInstance(inputStream, false);

· 上述解决了传入我们需要处理的图片，那么接下来就是显示指定的区域。

    bitmapRegionDecoder.decodeRegion(rect, options);

参数一很明显是一个rect，参数二是BitmapFactory.Options，你可以控制图片的inSampleSize,inPreferredConfig等。

然后，自定义显示一个大图控件。