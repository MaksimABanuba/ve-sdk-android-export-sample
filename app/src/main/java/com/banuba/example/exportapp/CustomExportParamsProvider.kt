package com.banuba.example.exportapp

import android.net.Uri
import androidx.core.net.toFile
import com.banuba.sdk.core.VideoResolution
import com.banuba.sdk.core.ext.toPx
import com.banuba.sdk.core.media.MediaFileNameHelper
import com.banuba.sdk.export.data.ExportParams
import com.banuba.sdk.export.data.ExportParamsProvider
import com.banuba.sdk.ve.domain.VideoRangeList
import com.banuba.sdk.ve.effects.Effects
import com.banuba.sdk.ve.effects.music.MusicEffect
import com.banuba.sdk.ve.effects.watermark.WatermarkAlignment
import com.banuba.sdk.ve.effects.watermark.WatermarkBuilder
import com.banuba.sdk.ve.ext.withWatermark

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
        val exportDestDir = exportDir.toFile().apply {
            deleteRecursively()
            mkdirs()
        }

        // Define separate audio track
        val separateAudioUri = if (exportAudioProvider.isEnable) {
            Uri.parse(exportDestDir.toString()).buildUpon()
                .appendPath(mediaFileNameHelper.generateExportSoundtrackFileName())
                .build()
        } else Uri.EMPTY

        // Defines params for the first video to export with HD video resolution and watermark.
        val firstHdWithWatermark =
            ExportParams.Builder(VideoResolution.Exact.HD)
                .effects(effects.withWatermark(watermarkBuilder, WatermarkAlignment.BottomRight(marginRightPx = 16.toPx)))
                .fileName("first_export_default")
                .debugEnabled(true)
                .videoRangeList(videoRangeList)
                .destDir(exportDestDir)
                .musicEffects(musicEffects)
                .volumeVideo(videoVolume)
                .extraAudioFile(separateAudioUri)
                .build()

        // Defines params for the second video to export with 360p video resolution and watermark.
        val secondLowQualityParams =
            ExportParams.Builder(VideoResolution.Exact.VGA360)
                .effects(effects.withWatermark(watermarkBuilder, WatermarkAlignment.TopLeft(marginTopPx = 16.toPx)))
                .fileName("second_export_default")
                .debugEnabled(true)
                .videoRangeList(videoRangeList)
                .destDir(exportDestDir)
                .volumeVideo(videoVolume)
                .build()

        return listOf(firstHdWithWatermark, secondLowQualityParams)
    }
}
