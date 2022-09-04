package com.banuba.example.exportapp

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import com.banuba.example.exportapp.custom.CustomExportParamsProvider
import com.banuba.sdk.core.domain.ImageLoader
import com.banuba.sdk.export.data.BackgroundExportFlowManager
import com.banuba.sdk.export.data.ExportFlowManager
import com.banuba.sdk.export.data.ExportParamsProvider
import com.banuba.sdk.export.data.ForegroundExportFlowManager
import com.banuba.sdk.ve.R
import com.banuba.sdk.ve.effects.watermark.WatermarkProvider
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

/**
 * Defines if export should provide track as a separate audio file.
 */
interface EnableExportAudioProvider {
    var isEnable: Boolean
}

class ExportSampleKoinModule {

    val module = module {

        /**
         * Override to run export in foreground.
         */
        single<ExportFlowManager>(named("foregroundExportFlowManager")) {
            ForegroundExportFlowManager(
                exportDataProvider = get(),
                sessionParamsProvider = get(),
                exportSessionHelper = get(),
                exportDir = get(named("exportDir")),
                publishManager = get(),
                errorParser = get(),
                mediaFileNameHelper = get(),
                exportBundleProvider = get()
            )
        }

        /**
         * Override to run export in background.
         */
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

        /**
         * Override to provide your application specific export parameters.
         */
        factory<ExportParamsProvider> {
            CustomExportParamsProvider(
                exportDir = get(named("exportDir")),
                mediaFileNameHelper = get(),
                watermarkBuilder = get(),
                exportAudioProvider = get()
            )
        }

        /**
         * Override to configure watermark on video.
         */
        single<WatermarkProvider> {
            object : WatermarkProvider {
                override fun getWatermarkBitmap(): Bitmap? = BitmapFactory.decodeResource(
                    androidContext().resources,
                    R.drawable.df_fsfw
                )
            }
        }

        /**
         * Override to produce separate audio track.
         */
        single<EnableExportAudioProvider> {
            object : EnableExportAudioProvider {
                override var isEnable: Boolean = false
            }
        }

        single<ImageLoader> {
            StubImageLoader()
        }
    }
}

/**
 * Stub implementation of ImageLoader.
 * You can provide your own implementation to load thumbnails, images, bitmap, stickers and gifs.
 */
class StubImageLoader : ImageLoader {

    override fun loadThumbnail(
        view: ImageView,
        uri: Uri,
        placeholderRes: Int?,
        errorPlaceholderRes: Int?,
        isCircle: Boolean,
        onResourceReady: (Drawable) -> Unit,
        onFailed: () -> Unit
    ) {
    }

    override fun loadImage(
        view: ImageView,
        uri: Uri,
        placeholderRes: Int?,
        errorPlaceholderRes: Int?,
        isCircle: Boolean,
        cornerRadiusPx: Int,
        skipCache: Boolean
    ) {
    }

    override fun getImageBitmap(uri: Uri, skipCache: Boolean): Bitmap {
        return Bitmap.createBitmap(0, 0, Bitmap.Config.ARGB_8888)
    }

    override fun loadSticker(
        view: ImageView,
        uri: Uri,
        isHiRes: Boolean,
        width: Int,
        height: Int
    ) {
    }

    override fun loadGif(view: ImageView, uri: Uri, onResourceReady: (Drawable) -> Unit) {}
}