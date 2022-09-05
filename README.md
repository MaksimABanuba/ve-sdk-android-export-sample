[![](https://www.banuba.com/hubfs/Banuba_November2018/Images/Banuba%20SDK.png)](https://www.banuba.com/video-editor-sdk)

# Android AI Video Editor Export API

This repository demonstrates how to use AI Video Editor Export API in your Android app.

- [Features](#Features)
- [Requirements](#Requirements)
- [Dependencies](#Dependencies)
- [Integration](#Integration)
- [API Reference](#API-Reference)

## Features

Export API can apply configurable effects to video and export that video with specified parameters.

| Feature             |                                      |
|---------------------|--------------------------------------|
| video formats       | .mp4, .mov, .m4v                     |
| video quality       | 360p, 480p, 540p, 720p, 1080p        |
| audio formats       | .mp3, .aac, .wav, .m4a, .flac, .aiff |
| modes               | background, foreground               |
| has UI              | :x:                                  |
| multiples files     | :white_check_mark:                   |
| visual effects      | :white_check_mark:                   |
| time effects        | :white_check_mark:                   |
| color effects       | :white_check_mark:                   |
| AR effects          | :white_check_mark:                   |
| text                | :white_check_mark:                   |
| gif                 | :white_check_mark:                   |
| watermark           | :white_check_mark:                   |
| custom aspect ratio | :white_check_mark:                   |

<br>

__Supported video quality__

| Quality          | Bitrate(kb/s) |
|------------------|---------------|
| 240p(240 x 352)  | 1000          |
| 360p(360 x 640)  | 1200          |
| 480p(480 x 854)  | 2000          |
| 540p(540 x 960)  | 2400          |
| HD(720 x 1280)   | 4000          |
| FHD(1080 x 1920) | 6400          |
| QHD(1440 x 2560) | 10000         |
| UHD(2160 x 3860) | 20000         |

## Requirements
This is what you need to run the Export API
- Java 1.8+
- Kotlin 1.4+
- Android Studio 4+
- Android OS 6.0 or higher
- OpenGL ES 3.0  

## Dependencies
- [Koin](https://insert-koin.io/)
- [ExoPlayer](https://github.com/google/ExoPlayer)
- [Kotlin Coroutines](https://github.com/Kotlin/kotlinx.coroutines)
- [ffmpeg](https://github.com/FFmpeg/FFmpeg/tree/n3.4.1)
- [AndroidX](https://developer.android.com/jetpack/androidx) libraries

[Please see all used dependencies](mddocs/all_dependencies.md)

## Integration
### Token
We offer а free 14-days trial for you could thoroughly test and assess Export API functionality in your app.

To get access to your trial, please, get in touch with us by [filling a form](https://www.banuba.com/video-editor-sdk) on our website. Our sales managers will send you the trial token.

### Getting Started  

Export API integration does not require much effort and pretty straightforward process.
1. __Add Banuba token__ <br/>
   The token **IS REQUIRED** to run sample and VE Export API integration in your app. For simplicity the token is put in [resources](https://github.com/Banuba/ve-sdk-android-export-sample/blob/master/app/src/main/res/values/strings.xml#L6).<br/><br/>
2. __Add dependencies to your .gradle file__ <br/>
   VE Export API contains a number of functional Android modules that should be declared in .gradle file.<br/><br/>
3. __Configure DI(Dependency Injection)__ <br/>
    **Koin** as a Dependency Injection is used to simplify integration and add more customizations of VE Export Api.<br/><br/>
4. __Configure Export flow__ <br/>
    Here you initialize ```ExportFlowManager``` to set up your export flow:
   - Set mode(background, foreground) that meets your app requirements. 
   - Set number of video files(sources) that should be exported in target video file.
   - Set number of video files(target) to export with video quality.
   - Set effects. Please see step 6. <br/><br/>
5. __Observe Export execution__ <br/>
   Add ```Observer<ExportResult>``` to listen to results of export execution.<br/><br/>
6. __Configure effects__ <br/>
   Add different effects i.e. watermark, visual(fx), time, text, gif, etc. to your exported video.<br/><br/>


[Please see full integration guide](mddocs/integration.md)


## API Reference
Please visit [API Reference](mddocs/index.md) to read more about ```ve-export-sdk``` module.


