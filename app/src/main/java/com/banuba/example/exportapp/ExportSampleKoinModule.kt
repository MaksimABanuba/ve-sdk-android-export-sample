package com.banuba.example.exportapp

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.banuba.example.exportapp.internal.EnableExportAudioProvider
import com.banuba.example.exportapp.utils.StubImageLoader
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

class ExportSampleKoinModule {

    val module = module {

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

        factory<ExportParamsProvider> {
            CustomExportParamsProvider(
                exportDir = get(named("exportDir")),
                mediaFileNameHelper = get(),
                watermarkBuilder = get(),
                exportAudioProvider = get()
            )
        }

        single<WatermarkProvider> {
            object : WatermarkProvider {
                override fun getWatermarkBitmap(): Bitmap? = BitmapFactory.decodeResource(
                    androidContext().resources,
                    R.drawable.df_fsfw
                )
            }
        }

        single<ImageLoader> {
            StubImageLoader()
        }

        single<EnableExportAudioProvider> {
            object : EnableExportAudioProvider {
                override var isEnable: Boolean = false
            }
        }
    }
}