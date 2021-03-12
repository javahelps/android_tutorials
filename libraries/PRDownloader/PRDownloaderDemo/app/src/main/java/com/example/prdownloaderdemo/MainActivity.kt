package com.example.prdownloaderdemo

import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.downloader.Error
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import com.downloader.PRDownloaderConfig
import java.io.BufferedReader
import java.io.FileNotFoundException
import java.io.InputStreamReader


class MainActivity : AppCompatActivity() {

    private lateinit var progressBar: ProgressBar
    private lateinit var btnDownload: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Find the widgets
        this.progressBar = findViewById(R.id.progressBar)
        this.btnDownload = findViewById(R.id.btnDownload)

        // Initialize PRDownloader with read and connection timeout
        val config = PRDownloaderConfig.newBuilder()
            .setReadTimeout(30000)
            .setConnectTimeout(30000)
            .build()
        PRDownloader.initialize(applicationContext, config)

        this.btnDownload.setOnClickListener {
            val url =
                "https://raw.githubusercontent.com/javahelps/externalsqliteimporter/master/README.md"
            val fileName = "readme.md"

            download(url, fileName)
        }
    }

    private fun download(url: String, fileName: String) {
        PRDownloader.download(
            url,
            baseContext.filesDir.absolutePath,
            fileName
        )
            .build()
            .setOnProgressListener {
                // Update the progress
                progressBar.max = it.totalBytes.toInt()
                progressBar.progress = it.currentBytes.toInt()
            }
            .start(object : OnDownloadListener {
                override fun onDownloadComplete() {
                    // Update the progress bar to show the completeness
                    progressBar.max = 100
                    progressBar.progress = 100

                    // Read the file
                    readFile(fileName)
                }

                override fun onError(error: Error?) {
                    Toast.makeText(baseContext, "Failed to download the $url", Toast.LENGTH_SHORT)
                        .show()
                }

            })
    }

    private fun readFile(fileName: String) {
        return try {
            val reader = BufferedReader(InputStreamReader(baseContext.openFileInput(fileName)))
            reader.use {
                val sb = StringBuilder()
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    sb.append(line)
                }
                val text = sb.toString()
                Toast.makeText(baseContext, text, Toast.LENGTH_LONG).show()
            }
        } catch (ex: FileNotFoundException) {
            Toast.makeText(baseContext, "Error in reading the file $fileName", Toast.LENGTH_SHORT)
                .show()
        }
    }
}