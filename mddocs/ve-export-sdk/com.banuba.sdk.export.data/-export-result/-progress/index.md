//[ve-export-sdk](../../../../index.md)/[com.banuba.sdk.export.data](../../index.md)/[ExportResult](../index.md)/[Progress](index.md)

# Progress

[androidJvm]\
data class [Progress](index.md)(preview: [Uri](https://developer.android.com/reference/kotlin/android/net/Uri.html), @[IntRange](https://developer.android.com/reference/kotlin/androidx/annotation/IntRange.html)(from = 0, to = 100)percentage: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)) : [ExportResult](../index.md)

Means that export is in progress. During the normal export flow [Progress](index.md) states may be sent: without preview image ([preview](preview.md) == Uri.EMPTY) - when the export just was started, with preview image - when the preview image is ready and export continues to be processed. These states are followed by the ones with [percentage](percentage.md) value changed. [percentage](percentage.md) value shows the relative percentage of export time. It is impossible to get a precise export progress because every video processing takes different amount of time.

## Parameters

androidJvm

| | |
|---|---|
| preview | Uri to the preview image |
| percentage | attracts the percentage of readiness for export |

## Constructors

| | |
|---|---|
| [Progress](-progress.md) | [androidJvm]<br>fun [Progress](-progress.md)(preview: [Uri](https://developer.android.com/reference/kotlin/android/net/Uri.html), @[IntRange](https://developer.android.com/reference/kotlin/androidx/annotation/IntRange.html)(from = 0, to = 100)percentage: [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) = 0) |

## Properties

| Name | Summary |
|---|---|
| [percentage](percentage.md) | [androidJvm]<br>val [percentage](percentage.md): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) = 0 |
| [preview](preview.md) | [androidJvm]<br>val [preview](preview.md): [Uri](https://developer.android.com/reference/kotlin/android/net/Uri.html) |
