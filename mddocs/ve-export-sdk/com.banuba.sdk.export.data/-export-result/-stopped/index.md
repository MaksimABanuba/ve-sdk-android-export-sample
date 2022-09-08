//[ve-export-sdk](../../../../index.md)/[com.banuba.sdk.export.data](../../index.md)/[ExportResult](../index.md)/[Stopped](index.md)

# Stopped

[androidJvm]\
data class [Stopped](index.md)(reason: [ExportStopReason](../../-export-stop-reason/index.md)) : [ExportResult](../index.md)

Means that export was started and stopped

## Parameters

androidJvm

| | |
|---|---|
| reason | is an [ExportStopReason](../../-export-stop-reason/index.md) object which was passed to the [ExportFlowManager.stopExport](../../-export-flow-manager/stop-export.md) function |

## Constructors

| | |
|---|---|
| [Stopped](-stopped.md) | [androidJvm]<br>fun [Stopped](-stopped.md)(reason: [ExportStopReason](../../-export-stop-reason/index.md)) |

## Properties

| Name | Summary |
|---|---|
| [reason](reason.md) | [androidJvm]<br>val [reason](reason.md): [ExportStopReason](../../-export-stop-reason/index.md) |
