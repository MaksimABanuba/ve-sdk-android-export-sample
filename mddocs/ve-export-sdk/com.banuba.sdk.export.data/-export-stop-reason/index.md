//[ve-export-sdk](../../../index.md)/[com.banuba.sdk.export.data](../index.md)/[ExportStopReason](index.md)

# ExportStopReason

[androidJvm]\
enum [ExportStopReason](index.md) : [Enum](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-enum/index.html)&lt;[ExportStopReason](index.md)&gt; 

The reason for the export stop which is passed into [ExportFlowManager.stopExport](../-export-flow-manager/stop-export.md) function to differentiate flows. It can be extracted from [ExportResult](../-export-result/index.md) object to handle any stop reason accordingly.

## Entries

| | |
|---|---|
| [INTERRUPT](-i-n-t-e-r-r-u-p-t/index.md) | [androidJvm]<br>[INTERRUPT](-i-n-t-e-r-r-u-p-t/index.md)()<br>Export is being interrupted by the external reason (i.e. app went into background during export) |
| [CANCEL](-c-a-n-c-e-l/index.md) | [androidJvm]<br>[CANCEL](-c-a-n-c-e-l/index.md)()<br>Export is being cancelled by the user |

## Properties

| Name | Summary |
|---|---|
| [name](-c-a-n-c-e-l/index.md#-372974862%2FProperties%2F545878494) | [androidJvm]<br>val [name](-c-a-n-c-e-l/index.md#-372974862%2FProperties%2F545878494): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [ordinal](-c-a-n-c-e-l/index.md#-739389684%2FProperties%2F545878494) | [androidJvm]<br>val [ordinal](-c-a-n-c-e-l/index.md#-739389684%2FProperties%2F545878494): [Int](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
