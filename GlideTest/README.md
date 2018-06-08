最近在又一次研读郭神的 [Glide最全解析](https://blog.csdn.net/column/details/15318.html) 系列，感觉每一次看都会有更深的体会和收获，不愧是郭神呀！郭神写的文章总能分析到位，深入原理的同时又能做到易于理解，给予我们思路和方法。这个专栏对Glide进行了全面的讲解，包括Glide的基本用法、源码解析、高级用法、功能扩展等内容，可能会是目前互联网上最详尽的Glide教程。一边在认真学习的我，产生了拿个小本本记下来的想法，于是就有了这个笔记，算是做些记录吧！

### 一、[Android图片加载框架最全解析（一），Glide的基本用法](https://blog.csdn.net/guolin_blog/article/details/53759439)
**基本用法**
引入库，申明权限，加载图片

    Glide.with(this).load(url).into(imageView);
    }

主要涉及到3个方法with()、load()、into()

**占位图**

    Glide.with(this)
     .load(url)
     .placeholder(R.drawable.loading)
     .error(R.drawable.error)
     .into(imageView);

**指定图片格式**
Glide是支持加载GIF图片的，而使用Glide加载GIF图并不需要编写什么额外的代码，Glide内部会自动判断图片格式。那加入希望加载的这张图必须是一张静态图片，我不需要Glide自动帮我判断它到底是静图还是GIF图呢？

    Glide.with(this)
     .load(url)
     .asBitmap()
     .into(imageView);

asBitmap()方法，这个方法的意思就是说这里只允许加载静态图片！由于调用了asBitmap()方法，现在GIF图就无法正常播放了，而是会在界面上显示第一帧的图片。

**指定图片大小**
使用Glide在绝大多数情况下我们都是不需要指定图片大小的。如果你真的有这样的需求，必须给图片指定一个固定的大小，Glide仍然是支持这个功能的。

    Glide.with(this)
     .load(url)
     .placeholder(R.drawable.loading)
     .error(R.drawable.error)
     .diskCacheStrategy(DiskCacheStrategy.NONE)
     .override(100, 100)
     .into(imageView);

使用Glide，我们就完全不用担心图片内存浪费，甚至是内存溢出的问题。因为Glide从来都不会直接将图片的完整尺寸全部加载到内存中，而是用多少加载多少。Glide会自动判断ImageView的大小，然后只将这么大的图片像素加载到内存当中，帮助我们节省内存开支。

### 二、[Android图片加载框架最全解析（二），从源码的角度理解Glide的执行流程](https://blog.csdn.net/guolin_blog/article/details/53939176)
Glide最基本的用法就是三步走，with()、load()、into()，还不赶快阅读原文！

### 三、[Android图片加载框架最全解析（三），深入探究Glide的缓存机制](https://blog.csdn.net/guolin_blog/article/details/54895665)
**Glide缓存简介**
在缓存这一功能上，Glide又将它分成了两个模块，一个是内存缓存，一个是硬盘缓存。

这两个缓存模块的作用各不相同，内存缓存的主要作用是防止应用重复将图片数据读取到内存当中，而硬盘缓存的主要作用是防止应用重复从网络或其他地方重复下载和读取数据。

内存缓存和硬盘缓存的相互结合才构成了Glide极佳的图片缓存效果。

**缓存Key**
既然是缓存功能，就必然会有用于进行缓存的Key。

Glide的缓存Key生成规则非常繁琐，决定缓存Key的参数竟然有10个之多。10个参数一起传入到EngineKeyFactory的buildKey()方法当中，从而构建出了一个EngineKey对象，这个EngineKey也就是Glide中的缓存Key了。

可见，决定缓存Key的条件非常多，即使你用override()方法改变了一下图片的width或者height，也会生成一个完全不同的缓存Key。

**内存缓存**
默认情况下，Glide自动就是开启内存缓存的。

禁用内存缓存功能：

    Glide.with(this)
     .load(url)
     .skipMemoryCache(true)
     .into(imageView);

Glide内存缓存的实现自然也是使用的LruCache算法。不过除了LruCache算法之外，Glide还结合了一种弱引用的机制，共同完成了内存缓存功能。

Glide的图片加载过程中会调用两个方法来获取内存缓存，loadFromCache()和loadFromActiveResources()。这两个方法中一个使用的就是LruCache算法，另一个使用的就是弱引用。

正在使用中的图片使用弱引用来进行缓存，不在使用中的图片使用LruCache来进行缓存的功能。

这就是Glide内存缓存的实现原理。

**磁盘缓存**
调用diskCacheStrategy()方法并传入DiskCacheStrategy.NONE，就可以禁用掉Glide的硬盘缓存功能了。

    Glide.with(this)
     .load(url)
     .diskCacheStrategy(DiskCacheStrategy.NONE)
     .into(imageView);

这个diskCacheStrategy()方法基本上就是Glide硬盘缓存功能的一切，它可以接收四种参数：

* DiskCacheStrategy.NONE： 表示不缓存任何内容。
* DiskCacheStrategy.SOURCE： 表示只缓存原始图片。
* DiskCacheStrategy.RESULT： 表示只缓存转换过后的图片（默认选项）。
* DiskCacheStrategy.ALL ： 表示既缓存原始图片，也缓存转换过后的图片。

有一个概念大家需要了解，就是当我们使用Glide去加载一张图片的时候，Glide默认并不会将原始图片展示出来，而是会对图片进行压缩和转换（我们会在后面学习这方面的内容）。总之就是经过种种一系列操作之后得到的图片，就叫转换过后的图片。而Glide默认情况下在硬盘缓存的就是转换过后的图片，我们通过调用diskCacheStrategy()方法则可以改变这一默认行为。

首先，和内存缓存类似，硬盘缓存的实现也是使用的LruCache算法，而且Google还提供了一个现成的工具类DiskLruCache。当然，Glide是使用的自己编写的DiskLruCache工具类，但是基本的实现原理都是差不多的。

**高级用法**
图片url地址中带有token参数的处理

### 四、[Android图片加载框架最全解析（四），玩转Glide的回调与监听](https://blog.csdn.net/guolin_blog/article/details/70215985)
**回调的源码实现**

**into()方法**
into()方法中是可以传入ImageView的。那么into()方法还可以传入别的参数吗？我可以让Glide加载出来的图片不显示到ImageView上吗？答案是肯定的，这就需要用到自定义Target功能。

into()方法还有一个接收Target参数的重载。即使我们传入的参数是ImageView，Glide也会在内部自动构建一个Target对象。而如果我们能够掌握自定义Target技术的话，就可以更加随心所欲地控制Glide的回调了。

Target的继承结构还是相当复杂的，实现Target接口的子类非常多。不过你不用被这么多的子类所吓到，这些大多数都是Glide已经实现好的具备完整功能的Target子类，如果我们要进行自定义的话，通常只需要在两种Target的基础上去自定义就可以了，一种是SimpleTarget，一种是ViewTarget。

**preload()方法**
Glide专门给我们提供了预加载的接口，也就是preload()方法。

preload()方法有两个方法重载，一个不带参数，表示将会加载图片的原始尺寸，另一个可以通过参数指定加载图片的宽和高。

preload()方法的用法也非常简单，直接使用它来替换into()方法即可，如下所示：

    Glide.with(this)
     .load(url)
     .diskCacheStrategy(DiskCacheStrategy.SOURCE)
     .preload();

需要注意的是，我们如果使用了preload()方法，最好要将diskCacheStrategy的缓存策略指定成DiskCacheStrategy.SOURCE。因为preload()方法默认是预加载的原始图片大小，而into()方法则默认会根据ImageView控件的大小来动态决定加载图片的大小。因此，如果不将diskCacheStrategy的缓存策略指定成DiskCacheStrategy.SOURCE的话，很容易会造成我们在预加载完成之后再使用into()方法加载图片，却仍然还是要从网络上去请求图片这种现象。

调用了预加载之后，我们以后想再去加载这张图片就会非常快了，因为Glide会直接从缓存当中去读取图片并显示出来，代码如下所示：

    Glide.with(this)
     .load(url)
     .diskCacheStrategy(DiskCacheStrategy.SOURCE)
     .into(imageView);

注意，这里我们仍然需要使用diskCacheStrategy()方法将硬盘缓存策略指定成DiskCacheStrategy.SOURCE，以保证Glide一定会去读取刚才预加载的图片缓存。

**downloadOnly()方法**
和preload()方法类似，downloadOnly()方法也是可以替换into()方法的。downloadOnly()方法表示只会下载图片，而不会对图片进行加载。当图片下载完成之后，我们可以得到图片的存储路径，以便后续进行操作。

downloadOnly()方法是定义在DrawableTypeRequest类当中的，它有两个方法重载，一个接收图片的宽度和高度，另一个接收一个泛型对象，如下所示：

* downloadOnly(int width, int height)
* downloadOnly(Y target)

这两个方法各自有各自的应用场景，其中downloadOnly(int width, int height)是用于在子线程中下载图片的，而downloadOnly(Y target)是用于在主线程中下载图片的。

**listener()方法**
不同于刚才几个方法都是要替换into()方法的，listener()是结合into()方法一起使用的，当然也可以结合preload()方法一起使用。

    public void loadImage(View view) {
    String url = "http://cn.bing.com/az/hprichbg/rb/TOAD_ZH-CN7336795473_1920x1080.jpg";
    Glide.with(this)
            .load(url)
            .listener(new RequestListener<String, GlideDrawable>() {
                @Override
                public boolean onException(Exception e, String model, Target<GlideDrawable> target,
                    boolean isFirstResource) {
                    return false;
                }

                @Override
                public boolean onResourceReady(GlideDrawable resource, String model,
                    Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                    return false;
                }
            })
            .into(imageView);
}

### 五、[Android图片加载框架最全解析（五），Glide强大的图片变换功能](https://blog.csdn.net/guolin_blog/article/details/71524668)
在前面四篇文章的当中，我们已经学习了Glide的基本用法、Glide的工作原理和执行流程、Glide的缓存机制、以及Glide的回调机制等内容。如果你能将前面的四篇文章都掌握好了，那么恭喜你，现在你已经是一名Glide好手了。

**图片变换的基本用法**
图片变换的意思就是说，Glide从加载了原始图片到最终展示给用户之前，又进行了一些变换处理，从而能够实现一些更加丰富的图片效果，如图片圆角化、圆形化、模糊化等等。

添加图片变换的用法非常简单，我们只需要调用transform()方法，并将想要执行的图片变换操作作为参数传入transform()方法即可，如下所示：

    Glide.with(this)
     .load(url)
     .transform(...)
     .into(imageView);

至于具体要进行什么样的图片变换操作，这个通常都是需要我们自己来写的。不过Glide已经内置了两种图片变换操作，我们可以直接拿来使用，一个是CenterCrop，一个是FitCenter。

但这两种内置的图片变换操作其实都不需要使用transform()方法，Glide为了方便我们使用直接提供了现成的API：

    Glide.with(this)
     .load(url)
     .centerCrop()
     .into(imageView);

    Glide.with(this)
     .load(url)
     .fitCenter()
     .into(imageView);

当然，centerCrop()和fitCenter()方法其实也只是对transform()方法进行了一层封装而已，它们背后的源码仍然还是借助transform()方法来实现的。

不过不得不说，Glide内置的图片变换接口功能十分单一且有限，完全没有办法满足我们平时的开发需求。因此，掌握自定义图片变换功能就显得尤为重要了。

**自定义图片变换**

**更多图片变换功能**
Glide的图片变换开源库，其中做的最出色的应该要数glide-transformations这个库了。它实现了很多通用的图片变换效果，如裁剪变换、颜色变换、模糊变换等等，使得我们可以非常轻松地进行各种各样的图片变换。

glide-transformations的项目主页地址是 [https://github.com/wasabeef/glide-transformations](https://github.com/wasabeef/glide-transformations) 。

### 六、[Android图片加载框架最全解析（六），探究Glide的自定义模块功能](https://blog.csdn.net/guolin_blog/article/details/78179422)
**自定义模块的基本用法**
自定义模块功能可以将更改Glide配置，替换Glide组件等操作独立出来，使得我们能轻松地对Glide的各种配置进行自定义，并且又和Glide的图片加载逻辑没有任何交集，这也是一种低耦合编程方式的体现。

首先需要定义一个我们自己的模块类，并让它实现GlideModule接口，如下所示：

    public class MyGlideModule implements GlideModule {
        @Override
        public void applyOptions(Context context, GlideBuilder builder) {
        }

        @Override
        public void registerComponents(Context context, Glide glide) {
        }
    }

可以看到，在MyGlideModule类当中，我们重写了applyOptions()和registerComponents()方法，这两个方法分别就是用来更改Glide和配置以及替换Glide组件的。我们待会儿只需要在这两个方法中加入具体的逻辑，就能实现更改Glide配置或者替换Glide组件的功能了。

不过，目前Glide还无法识别我们自定义的MyGlideModule，如果想要让它生效，还得在AndroidManifest.xml文件当中加入如下配置才行：

    <manifest>

    ...

    <application>

        <meta-data
            android:name="com.example.glidetest.MyGlideModule"
            android:value="GlideModule" />

        ...

    </application>
</manifest>  

在<application>标签中加入一个meta-data配置项，其中android:name指定成我们自定义的MyGlideModule的完整路径，android:value必须指定成GlideModule，这个是固定值。

这样的话，我们就将Glide自定义模块的功能完成了，是不是非常简单？现在Glide已经能够识别我们自定义的这个MyGlideModule了。

**自定义模块的原理**

**更改Glide配置**
如果想要更改Glide的默认配置，其实只需要在applyOptions()方法中提前将Glide的配置项进行初始化就可以了。

Glide一共有哪些配置项呢？
* setMemoryCache() 
用于配置Glide的内存缓存策略，默认配置是LruResourceCache。

* setBitmapPool() 
用于配置Glide的Bitmap缓存池，默认配置是LruBitmapPool。

* setDiskCache() 
用于配置Glide的硬盘缓存策略，默认配置是InternalCacheDiskCacheFactory。

* setDiskCacheService() 
用于配置Glide读取缓存中图片的异步执行器，默认配置是FifoPriorityThreadPoolExecutor，也就是先入先出原则。

* setResizeService() 
用于配置Glide读取非缓存中图片的异步执行器，默认配置也是FifoPriorityThreadPoolExecutor。

* setDecodeFormat() 
用于配置Glide加载图片的解码模式，默认配置是RGB_565。

其实Glide的这些默认配置都非常科学且合理，使用的缓存算法也都是效率极高的，因此在绝大多数情况下我们并不需要去修改这些默认配置，这也是Glide用法能如此简洁的一个原因。

但是Glide科学的默认配置并不影响我们去学习自定义Glide模块的功能，因此总有某些情况下，默认的配置可能将无法满足你，这个时候就需要我们自己动手来修改默认配置了。

**替换Glide组件**
替换Glide组件功能需要在自定义模块的registerComponents()方法中加入具体的替换逻辑。相比于更改Glide配置，替换Glide组件这个功能的难度就明显大了不少。Glide中的组件非常繁多，也非常复杂，但其实大多数情况下并不需要我们去做什么替换。不过，有一个组件却有着比较大的替换需求，那就是Glide的HTTP通讯组件。

默认情况下，Glide使用的是基于原生HttpURLConnection进行订制的HTTP通讯组件，但是现在大多数的Android开发者都更喜欢使用OkHttp，因此将Glide中的HTTP通讯组件修改成OkHttp的这个需求比较常见。

**更简单的组件替换**
Glide官方给我们提供了非常简便的HTTP组件替换方式。

我们只需要在gradle当中添加几行库的配置就行了。比如使用OkHttp3来作为HTTP通讯组件的配置如下：

    dependencies {
        compile 'com.squareup.okhttp3:okhttp:3.9.0'
        compile 'com.github.bumptech.glide:okhttp3-integration:1.5.0@aar'
    }

### 七、[Android图片加载框架最全解析（七），实现带进度的Glide图片加载功能](https://blog.csdn.net/guolin_blog/article/details/78357251)
对Glide进行功能扩展，使其支持监听图片下载进度的功能。

将HTTP通讯组件替换成OkHttp之后，我们又该如何去实现监听下载进度的功能呢？这就要依靠OkHttp强大的拦截器机制了。

我们只要向OkHttp中添加一个自定义的拦截器，就可以在拦截器中捕获到整个HTTP的通讯过程，然后加入一些自己的逻辑来计算下载进度，这样就可以实现下载进度监听的功能了。

转载请注明出处：[http://blog.csdn.net/guolin_blog/article/details/54895665](http://blog.csdn.net/guolin_blog/article/details/54895665)