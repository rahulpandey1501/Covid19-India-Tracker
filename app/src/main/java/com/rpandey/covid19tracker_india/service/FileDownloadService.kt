package com.rpandey.covid19tracker_india.service

import com.rpandey.covid19tracker_india.data.Status
import com.rpandey.covid19tracker_india.data.RequestId
import com.rpandey.covid19tracker_india.network.APIProvider
import com.rpandey.covid19tracker_india.network.ApiHelper
import com.rpandey.covid19tracker_india.util.Util
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class FileDownloadService(private val apiProvider: APIProvider) {

    suspend fun startDownloading(downloadUrl: String, fileName: String, directoryPath: File, callback: suspend (Boolean, File?) -> Unit) {
        Util.log("Download Stared", downloadUrl)
        val downloadStatus = ApiHelper().handleRequest(RequestId.FILE_DOWNLOAD) {
            apiProvider.firebaseHostApiService.downloadFile(downloadUrl)
        }
        if (downloadStatus is Status.Success) {
            if (!directoryPath.exists()) {
                directoryPath.mkdir()
            }
            val downloadedFile = saveDownloadFile(downloadStatus.data.byteStream(), fileName, directoryPath)
            callback(downloadedFile != null, downloadedFile)

        } else {
            callback(false, null)
        }
        Util.log("Download Completed", downloadUrl)
    }

    private fun saveDownloadFile(inputStream: InputStream, fileName: String, directoryPath: File): File? {
        val file = File(directoryPath, fileName)
        try {
            inputStream.use { it ->
                FileOutputStream(file).use { output ->
                    val buffer = ByteArray(4 * 1024) // or other buffer size
                    var read: Int
                    while (it.read(buffer).also { read = it } != -1) {
                        output.write(buffer, 0, read)
                    }
                    output.flush()
                }

                return file
            }
        } catch (e: Exception) {
            file.delete()
            return null
        }
    }
}