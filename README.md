# AndroidHotFixManage
基于AndFix 和 Tinker 的 Android 热修复组件化以及Patch自动检测和管理

# 解决问题
基于 AndFix 和 Tinker 的再次封装，用户引入该 module 之后，只需要稍稍修改下请求自己服务器的 Patch 接口，就可以直接进行线上 Bug 的修复。

# 主要模块介绍
1. commonlibrary 基础库 主要是提供一些基础的 UI, Utils, View, Adapter, ViewHolder 等。也就是公共的基础库。
  
2. netlibrary 网络库 主要是针对 OkHttp 库的一些封装，实现各种网络请求，包括文件上传，下载，我们的热修复插件下载就可以直接用这个库里的方法。
  
3. hotfixlibrary 热修复库 主要是针对 Andfix Tinker 热修复的框架的封装，便于我们直接使用，不用关心具体逻辑。
  
4. app 主模块 用户程序的入口。
  
# 主要类及其方法介绍

## 1. 在 Application 类中，首先对部分模块进行初始化

```java
@Override
public void onCreate() {
       super.onCreate();
       sInstance = this;
       // 完成andFix的初始化
       AndFixPatchManager.getInstance().initPatch(this);
       // 完成网络请求组件的初始化
       RequestNetManager.init();
       // 上下文的赋值
       UIUtils.initContext(this);
 }
```

## 2. 在 SplashActivity 界面，启动服务去检查是否有新的热更新插件，同时比较版本

```java

    private void updatePatchCheck() {
        Request request = new Request();
        request.url = ServerAddress.S_AND_FIX.UPDATE_CHECK_URL;
        Map<String, String> params = new HashMap<>();
        // 大的版本号 -- 对应的就是我们的分支
        params.put("versionCode", AppUtils.getVersion(this));
        // 小的版本号(比如我们在同一个版本号上进行了多次修复)
        params.put("smallVersionCode", PreUtils.getInt(AppConstant.PRE_KEY_SMALL_VERSION_CODE, 0) + "");

        request.params = params;
        request.tag(ServerAddress.S_AND_FIX.DOWN_PATCH_URL);

        RequestNetManager.post(request, new CustomJsonCallback<PatchInfoResponse>() {
            @Override
            public void onSuccess(PatchInfoResponse responseObj) {
                String url = responseObj.data.downloadUrl;
                String smallVersionCode = responseObj.data.smallVersionCode;
                if (TextUtils.isEmpty(url)) {
                    stopSelf();
                    Log.i(TAG, "下载地址为空");
                } else {
                    if (!TextUtils.isEmpty(smallVersionCode)) {
                        // 判断小版本的code是否有更新
                        int smallVCode = Integer.parseInt(smallVersionCode);
                        if (smallVCode > PreUtils.getInt(AppConstant.PRE_KEY_SMALL_VERSION_CODE, 0)) {
                            mInnerHandler.sendEmptyMessage(DOWN_LOAD_PATCH);
                        } else {
                            stopSelf();
                        }
                    } else {
                        stopSelf();
                    }
                }
            }

            @Override
            public void onFailure(CustomOkHttpException exception) {
                String message = exception.message;
                Log.e(TAG, message);

            }
        });
    }
```    
    
 ## 3. 下载 patch 文件，并且直接加载
 
 ```java
 private void downloadPatch() {

        Request request = new FileRequest();
        request.url = ServerAddress.S_AND_FIX.DOWN_PATCH_URL;
        String destFileName = String.valueOf(System.currentTimeMillis()).concat(SUFFIX);

        mFixedPatchFile = mFixedPatchDir.concat(destFileName);

        // 传入目标文件夹和文件名
        RequestNetManager.download(request, new CustomFileCallback(mFixedPatchDir, destFileName) {
            @Override
            public void onSuccess(File responseObj) {
                // 这个需要绝对路径
                AndFixPatchManager.getInstance().addPatch(mFixedPatchFile);
            }

            @Override
            public void onFailure(CustomOkHttpException exception) {
                Log.e(TAG, exception.message);
            }

            @Override
            public void inProgress(float progress, long total, int id) {
                super.inProgress(progress, total, id);
                Log.i(TAG, "progress = " + progress);
            }
        });
    }
  ```
 ## 4. Tinker 接入与 andfix 大体一致
 
 #### a. 定义 applicationLike 继承 ApplicationLike， 并实现构造方法， 同时添加好 DefaultLifeCycle 注解
 
 ```java
 @DefaultLifeCycle(application = ".AndroidTinkerApplication",
    flags =  ShareConstants.TINKER_ENABLE_ALL,
    loadVerifyFlag = false
)
public class AndroidTinkerApplicationLike extends ApplicationLike {

    private static Context sContext;

    public static Context getContext() {
        return sContext;
    }

    public AndroidTinkerApplicationLike(Application application, int tinkerFlags, boolean tinkerLoadVerifyFlag, long applicationStartElapsedTime,
            long applicationStartMillisTime,
            Intent tinkerResultIntent) {
        super(application, tinkerFlags, tinkerLoadVerifyFlag, applicationStartElapsedTime, applicationStartMillisTime, tinkerResultIntent);
    }

    @Override
    public void onBaseContextAttached(Context base) {
        super.onBaseContextAttached(base);
        // 初始化Tinker
        TinkerFixPatchManager.installTinker(this);
        sContext =  getApplication();

        // 完成网络请求组件的初始化
        RequestNetManager.init();
        // 上下文的赋值
        UIUtils.initContext(sContext);

        Log.i("qinglin.fan", "application tinker");
    }
}
  ```
  
  #### b. 封装好 TinkerFixPatchManager, install, load patch的方法放在这里
  ```java
  /**
 * 对tinker热修复的封装
 */
public class TinkerFixPatchManager {

    private static boolean isInstalled = false;
    private static ApplicationLike mAppLike;

    /**
     * 完成Tinker的初始化
     */
    public static void installTinker(ApplicationLike applicationLike) {
        mAppLike = applicationLike;
        if (isInstalled) {
            return;
        }
        //完成tinker初始化
        TinkerInstaller.install(mAppLike);
        isInstalled = true;

        // add for tinker patch test
        System.out.print("tinker patch test");
    }

    //完成Patch文件的加载
    public static void loadPatch(String path) {
        if (Tinker.isTinkerInstalled()) {
            TinkerInstaller.onReceiveUpgradePatch(getApplicationContext(), path);
        }
    }

    //通过ApplicationLike获取Context
    private static Context getApplicationContext() {
        if (mAppLike != null) {
            return mAppLike.getApplication().getApplicationContext();
        }
        return null;
    }
}
  ```
 #### c. 启动 service 下载好 patch 文件后，进行load 
  ```java
  private void downloadPatch() {

        Request request = new FileRequest();
        request.url = ServerAddress.S_AND_FIX.DOWN_PATCH_URL;
        String destFileName = String.valueOf(System.currentTimeMillis()).concat(SUFFIX);

    mFixedPatchFile = mFixedPatchDir.concat(destFileName);

        // 传入目标文件夹和文件名
        RequestNetManager.download(request, new CustomFileCallback(mFixedPatchDir, destFileName) {
            @Override
            public void onSuccess(File responseObj) {
                // 这个需要绝对路径
               if (isUseTinker) {
                   TinkerFixPatchManager.loadPatch(mFixedPatchFile);
               } else {
                   AndFixPatchManager.getInstance().addPatch(mFixedPatchFile);
               }
            }

            @Override
            public void onFailure(CustomOkHttpException exception) {
                Log.e(TAG, exception.message);
            }

            @Override
            public void inProgress(float progress, long total, int id) {
                super.inProgress(progress, total, id);
                Log.i(TAG, "progress = " + progress);
            }
        });
    }
  ```
  #### d. gradle 文件中配置 tinkerPatch 生成 patch apk
  ```java
    tinkerPatch {

        oldApk = getOldApkPath() //指定old apk文件径

        ignoreWarning = false   //不忽略tinker的警告，有则中止patch文件的生成

        useSign = true  //强制patch文件也使用签名

        tinkerEnable = buildWithTinker(); //指定是否启用tinker

        buildConfig {

            applyMapping = getApplyMappingPath()  //指定old apk打包时所使用的混淆文件

            applyResourceMapping = getApplyResourceMappingPath()  //指定old apk的资源文件

            tinkerId = getTinkerIdValue() //指定TinkerID

            keepDexApply = false
        }

        dex {

            dexMode = "jar" //jar、raw
            pattern = ["classes*.dex", "assets/secondary-dex-?.jar"] //指定dex文件目录
            loader = ["com.fanql.androidfix.application.AndroidTinkerApplication"] //指定加载patch文件时用到的类
        }

        lib {

            pattern = ["libs/*/*.so"]
        }

        res {

            pattern = ["res/*", "assets/*", "resources.arcs", "AndoridManifest.xml"]
            //指定tinker可以修改的资源路径

            ignoreChange = ["assets/sample_meta.txt"] //指定不受影响的资源路径

            largeModSize = 100 //资源修改大小默认值
        }

        packageConfig {

            configField("patchMessage", "fix the 1.0 version's bugs")

            configField("patchVersion", "1.0")
        }
    }
   ```   
   #### e. manifest 文件中修改成 tinker 的 application
 ```java
   <application
        android:name=".application.AndroidTinkerApplication"
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/AppTheme"
        tools:ignore="AllowBackup,GoogleAppIndexingWarning">
        <activity android:name=".ui.SplashActivity">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />

                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>

        <activity android:name=".ui.MainActivity"/>
    </application>
  ```
