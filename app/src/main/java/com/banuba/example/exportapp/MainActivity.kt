package com.banuba.example.exportapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Size
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.banuba.sdk.core.Rotation
import com.banuba.sdk.core.domain.AspectRatioProvider
import com.banuba.sdk.core.domain.VideoSourceType
import com.banuba.sdk.core.ext.isNullOrEmpty
import com.banuba.sdk.core.media.DurationExtractor
import com.banuba.sdk.export.data.ExportFlowManager
import com.banuba.sdk.export.data.ExportResult
import com.banuba.sdk.export.data.ExportStopReason
import com.banuba.sdk.export.data.ExportTaskParams
import com.banuba.sdk.ve.domain.VideoRangeList
import com.banuba.sdk.ve.domain.VideoRecordRange
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.inject
import org.koin.core.qualifier.named
import java.io.File

/**
 * The sample demonstrates how to start export video using Export API in background or foreground modes.
 * - Background - when the user can interact with UI
 * - Foreground - when the user has to wait when export finishes.
 * Since export process requires initial video files we made quick integration with Gallery
 * that allows to choose video files and apply it in in export.
 */
class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private var isBackgroundExport = true
    private var exportResultVideoUri = Uri.EMPTY

    // Step1
    // Declare ExportFlowManager for your app - foreground or background(user can interact with UI).
    private val backgroundExportFlowManager: ExportFlowManager by inject(named("backgroundExportFlowManager"))
    private val foregroundExportFlowManager: ExportFlowManager by inject(named("foregroundExportFlowManager"))
    private val aspectRatioProvider: AspectRatioProvider by inject()

    // Step 2
    // Create result observer - all export execution results are delivered here.
    private val exportResultObserver = Observer<ExportResult> { exportResult ->
        when (exportResult) {
            is ExportResult.Progress -> {
                previewImageView.setImageURI(exportResult.preview)
                progressVisible(true)
            }

            is ExportResult.Success -> {
                progressVisible(false)
                exportResultVideoUri = exportResult.videoList.first().sourceUri
                Toast.makeText(
                    this,
                    "Export Success: ${exportResult.additionalExportData}",
                    Toast.LENGTH_SHORT
                ).show()
            }

            is ExportResult.Error -> {
                progressVisible(false)
                Toast.makeText(
                    this,
                    getString(exportResult.type.messageResId),
                    Toast.LENGTH_SHORT
                ).show()
            }

            is ExportResult.Inactive, is ExportResult.Stopped -> progressVisible(false)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Step 3
        // Observe export results depends on flow - foreground or background
        backgroundExportFlowManager.resultData.observe(this, exportResultObserver)
        foregroundExportFlowManager.resultData.observe(this, exportResultObserver)

        initViews()
    }

    /**
     * Step 4
     * Prepare VideoRangeList to start export.
     * It requires playFromMs and playToMs arguments to be set when creating the VideoRecordRange object.
     * Code below uses a range from 0 to video length for each video.
     */
    private fun prepareVideoRages(videosUri: List<Uri>): VideoRangeList {
        val videoRecords = videosUri.map { fileUri ->
            val videoDuration = DurationExtractor().extractDurationMilliSec(this, fileUri)
            val videoSpeed = 1f
            VideoRecordRange(
                sourceUri = fileUri,            //mandatory, uri of video file
                durationMs = videoDuration,     //mandatory, duration of video
                speed = videoSpeed,             //mandatory, video playback speed
                playFromMs = 0,                 //optional, by default equals 0
                playToMs = videoDuration,       //optional, by default equals duration of video,
                rotation = Rotation.ROTATION_0,  //optional, by default ROTATION_0
                type = VideoSourceType.GALLERY  //mandatory, type of video source (gallery, camera, slideshow)
            )
        }
        return VideoRangeList(videoRecords)
    }

    // Step 5
    // Start Export flow!
    private fun startExportFlow(videosUri: List<Uri>) {
        val videoRanges = prepareVideoRages(videosUri)

        val totalVideoDuration = videoRanges.data.sumOf { it.durationMs }

        val effects = ExportEffectsProvider().provideEffects(applicationContext, totalVideoDuration)

        val coverFrameSize = Size(720, 1280)

        val exportTaskParams = ExportTaskParams(
            videoRanges = videoRanges,
            effects = effects,
            musicEffects = emptyList(),
            videoVolume = 1F,
            coverFrameSize = coverFrameSize,
            aspect = aspectRatioProvider.provide()        //by default provided aspect ratio = 9.0 / 16
        )

        if (isBackgroundExport) {
            backgroundExportFlowManager.startExport(exportTaskParams)
        } else {
            foregroundExportFlowManager.startExport(exportTaskParams)
        }
    }

    /**
     * Sample specific code.
     */
    private fun initViews() {
        backgroundExportBtn.setOnClickListener {
            pickPredefinedVideos(true)
        }

        foregroundExportBtn.setOnClickListener {
            pickPredefinedVideos(false)
        }

        stopExportBtn.setOnClickListener {
            exportResultVideoUri = Uri.EMPTY
            if (isBackgroundExport) {
                backgroundExportFlowManager.stopExport(ExportStopReason.CANCEL)
            } else {
                foregroundExportFlowManager.stopExport(ExportStopReason.CANCEL)
            }
        }

        previewImageView.setOnClickListener {
            if (exportResultVideoUri.isNullOrEmpty()) return@setOnClickListener
            openVideo(exportResultVideoUri)
        }
    }

    private fun pickPredefinedVideos(isBackground: Boolean) {
        previewImageView.setImageURI(null)
        isBackgroundExport = isBackground

        pickSampleVideos.launch("video/*")
    }

    private val pickSampleVideos = registerForActivityResult(
        ActivityResultContracts.GetMultipleContents()
    ) { videosUri ->
        if (videosUri.isNullOrEmpty()) return@registerForActivityResult

        progressVisible(true)

        startExportFlow(videosUri)
    }

    private fun progressVisible(isExporting: Boolean) {
        if (!isBackgroundExport) {
            exportProgressView.isVisible = isExporting
        }
        backgroundExportBtn.isEnabled = !isExporting
        foregroundExportBtn.isEnabled = !isExporting
    }

    private fun openVideo(videoUri: Uri) {
        videoUri.encodedPath?.let { encodedPath ->
            val intent = Intent(Intent.ACTION_VIEW).apply {
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                val uri = FileProvider.getUriForFile(
                    applicationContext,
                    "$packageName.provider",
                    File(encodedPath)
                )
                setDataAndType(uri, "video/mp4")
            }
            startActivity(intent)
        }
    }
}