# Integration Guide

## Step 1 - Add Banuba Token

:exclamation: The token **IS REQUIRED** to run sample and an integration in your app.</br>
Add the token to [resources](https://github.com/Banuba/ve-sdk-android-export-sample/blob/master/app/src/main/res/values/strings.xml#L6).
</br>
## Step 2 - Add dependencies

Add Banuba repository to your [build.gradle](https://github.com/Banuba/ve-sdk-android-export-sample/blob/master/build.gradle#L21)

``` groovy
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/Banuba/banuba-ve-sdk")
            credentials {
                username = ...
                password = ...
            }
        }
```

Specify the following dependencies as in [app/build.gradle](https://github.com/Banuba/ve-sdk-android-export-sample/blob/master/app/build.gradle#L44) file to get functional modules of VE Export API.

``` groovy
def banubaSdkVersion = '1.24.1'
implementation "com.banuba.sdk:ffmpeg:4.4"
implementation "com.banuba.sdk:banuba-token-storage-sdk:${banubaSdkVersion}"
implementation "com.banuba.sdk:core-sdk:${banubaSdkVersion}"
implementation "com.banuba.sdk:ve-sdk:${banubaSdkVersion}"
implementation "com.banuba.sdk:ve-export-sdk:${banubaSdkVersion}"
```

## Step 3 - Configure Dependency Injection

Override behavior of VE Export API in an app with DI libraries and tools (we use [Koin](https://insert-koin.io/)).  
Create new KoinModule for export flow.
There is a number of properties you can override.
``` kotlin
class ExportSampleKoinModule() {
    val module = module {

        factory<ExportParamsProvider>(override = true) {
            CustomExportParamsProvider(
                exportDir = get(named("exportDir")),
                mediaFileNameHelper = get(),
                watermarkBuilder = get(),
                exportAudioProvider = get()
            )
        }
    }
}
```
<br></br>
Please see [full example](https://github.com/Banuba/ve-sdk-android-export-sample/blob/master/app/src/main/java/com/banuba/example/exportapp/ExportSampleKoinModule.kt).
<br></br>

Next, initialize Koin in your ```Application.onCreate()```  method.
``` kotlin
override fun onCreate() {
    super.onCreate()
    startKoin {
        androidContext(this@ExportApp)
        allowOverride(true)
        modules(
            VeSdkKoinModule().module,
            VeExportKoinModule().module,
            TokenStorageKoinModule().module,
            ExportSampleKoinModule().module,
        )
    }
}
```
<br></br>
Please see [full example](https://github.com/Banuba/ve-sdk-android-export-sample/blob/master/app/src/main/java/com/banuba/example/exportapp/ExportApp.kt#L16)

## Step 4 - Configure export flow

VE Export API produces video in various formats i.e. mp4 file, in different modes and with many effects.</br>

By default, exported videos are placed in the **export** directory on external storage. To change the target directory you should provide a custom ```Uri``` instance named **exportDir** in Koin module.</br>

``` kotlin
class ExportSampleKoinModule() {
    val module = module {
    
        ...

        factory<ExportParamsProvider>(override = true) {
            CustomExportParamsProvider(
                exportDir = get(named("exportDir")),
                mediaFileNameHelper = get(),
                watermarkBuilder = get(),
                exportAudioProvider = get()
            )
        }
        
        ...
    }
}
``` 

Create custom implementation of ```ExportParamsProvider``` to configure export output for your app.
Override single method - ```provideExportParams()``` to return ```List<ExportParams>```.
Every ```ExportParams``` in the list produces a corresponding video in **exportDir** property.</br>
For every exported video you can choose special video resolution using ```VideoResolution.Exact``` class.

``` kotlin
class CustomExportParamsProvider(
    private val exportDir: Uri,
    private val mediaFileNameHelper: MediaFileNameHelper,
    private val watermarkBuilder: WatermarkBuilder,
    private val exportAudioProvider: EnableExportAudioProvider
) : ExportParamsProvider {

    override fun provideExportParams(
        effects: Effects,
        videoRangeList: VideoRangeList,
        musicEffects: List<MusicEffect>,
        videoVolume: Float
    ): List<ExportParams> {
        // Define your destination directory where video should be stored
        ...

        // Defines params to export new video.
        val firstVideoParams =
            ExportParams.Builder(VideoResolution.Exact.HD)
                .effects(effects.withWatermark(watermarkBuilder, WatermarkAlignment.BottomRight(marginRightPx = 16.toPx)))
                .fileName("first_export_default")
                ...
                .build()
                
        val secondVideoParams =
            ExportParams.Builder(VideoResolution.Exact.VGA360)
                .effects(effects.withWatermark(watermarkBuilder, WatermarkAlignment.BottomRight(marginRightPx = 16.toPx)))
                .fileName("second_export_default")
                ...
                .build()

        return listOf(firstVideoParams, secondVideoParams)
    }
}
``` 
<br></br>
Please see [full example](https://github.com/Banuba/ve-sdk-android-export-sample/blob/master/app/src/main/java/com/banuba/example/exportapp/CustomExportParamsProvider.kt)
<br></br>
Next, configure export flow mode to produce video files.</br>
There are 2 modes:
+ Foreground **(Default)** - export runs in the foreground. For example, the user see progress indication till export finishes. No need to add it in Koin module.
+ Background - export runs in the background. The user can leave a screen and notification will be sent when video is ready.

Override ``` ExportFlowManager``` in Koin module to run export in the background.
``` kotlin
class ExportSampleKoinModule() {
    val module = module {
        ...
        single<ExportFlowManager>(named("backgroundExportFlowManager")) {
            BackgroundExportFlowManager(
                exportDataProvider = get(),
                sessionParamsProvider = get(),
                exportSessionHelper = get(),
                exportNotificationManager = get(),
                exportDir = get(named("exportDir")),
                publishManager = get(),
                errorParser = get(),
                exportBundleProvider = get()
            )
        }
        ...
    }
}
```  

Please see both export modes in [full example](https://github.com/Banuba/ve-sdk-android-export-sample/blob/master/app/src/main/java/com/banuba/example/exportapp/ExportSampleKoinModule.kt#L48)</br>
<br>

Last, start export by creating an instance of ```ExportTaskParams``` and pass to ``` ExportFlowManager.startExport()``` function.
``` kotlin
/**
 * Data class which contains video, effects, music and cover params used in export.
 * It is passed into [ExportFlowManager.startExport] function.
 *
 * @param videoRanges [VideoRangeList] object containing video clips
 * @param effects [Effects] visual and time effects to apply to the exported video
 * @param musicEffects list of music effects applied to the exported video
 * @param videoVolume volume of exported video specified in float number from 0 to 1
 * @param coverUri Uri of the cover image file
 * @param coverFrameSize size of the cover image
 * @param aspect aspect ratio applied to the exported video
 * @param videoResolution optional [VideoResolution] value applied to the exported video.
 *  By default the optimal size will be set automatically (it is calculated with taking into
 *  account capabilities of the device)
 * @param additionalExportData any Parcelable object that may be received
 *  in [ExportResult.Success.additionalExportData] parameter
 * @param doOnStart lambda the will be invoked in the very beginning of the export process
 */
data class ExportTaskParams(
    val videoRanges: VideoRangeList,
    val effects: Effects,
    val musicEffects: List<MusicEffect>,
    val videoVolume: Float,
    var coverUri: Uri = Uri.EMPTY,
    val coverFrameSize: Size,
    val aspect: AspectRatio,
    val videoResolution: VideoResolution.Exact? = null,
    var additionalExportData: Parcelable? = null,
    var doOnStart: (() -> Unit)? = null
)
```
Please see [full example](https://github.com/Banuba/ve-sdk-android-export-sample/blob/master/app/src/main/java/com/banuba/example/exportapp/MainActivity.kt#L123).


## Step 5 - Configure effects
VE Export API supports a number of effects on video i.e. watermark, text, gif, visual(fx), etc.

__Watermark on video__</br>
Create custom implementation of ```WatermarkProvider``` in Koin Module.

Please see [full example](https://github.com/Banuba/ve-sdk-android-export-sample/blob/master/app/src/main/java/com/banuba/example/exportapp/ExportSampleKoinModule.kt#L76).
<br></br>

__Text on video__</br>
Create an instance of ```TextObjectDrawable```.

__GIF on video__</br>
Create an instance of ```GifObjectDrawable```.

__Visual and Time effects__</br>
Add dependency

``` groovy
implementation "com.banuba.sdk:ve-effects-sdk:${banubaSdkVersion}"
```
and see [full example](https://github.com/Banuba/ve-sdk-android-export-sample/blob/master/app/src/main/java/com/banuba/example/exportapp/ExportEffectsProvider.kt).
<br></br>


:exclamation: You can use **visual(fx)** and **time** effects only available to you according to the pricing plan.
Trying to use out of plan effects will lead to an exception.

## Step 6: Observe export execution

Create an instance of ```Observer<ExportResult>``` and pass it to ```exportFlowManager.resultData.observe(this, exportResultObserver)```.</br>
You can override the following states:
- Inactive
- Stopped
- Progress
- Error
- Success

``` kotlin
sealed class ExportResult {
    object Inactive : ExportResult()

    object Stopped : ExportResult()

    data class Progress(val preview: Uri) : ExportResult()

    @Parcelize
    data class Success(
        val videoList: List<ExportedVideo>,
        val preview: Uri,
        val metaUri: Uri,
        val additionalExportData: Bundle
    ) : ExportResult(), Parcelable

    @Parcelize
    data class Error(val type: ExportError) : ExportResult(), Parcelable
}
```

Results are delivered to observer after ```ExportFlowManager.startExport()``` call.

Only for export running in background mode</br>
override ```ExportNotificationManager``` to change notifications for export results (started, finished, etc.).</br>

Please see [full example](https://github.com/Banuba/ve-sdk-android-export-sample/blob/master/app/src/main/java/com/banuba/example/exportapp/MainActivity.kt#48).
<br>

## Step 7(Optional) - Custom export flow

To achieve fully control over export flow you can create custom implementation of ```ExportFlowManager```.
``` kotlin
interface ExportFlowManager {
    val resultData: LiveData<ExportResult>
    val provideExportInBackground: Boolean

    fun startExport(exportTaskParams: ExportTaskParams)
    fun stopExport()
    fun setInactive()
}
```


