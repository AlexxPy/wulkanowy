package io.github.wulkanowy.data.repositories.logger

import android.content.Context
import io.reactivex.Single
import java.io.File
import java.nio.charset.Charset
import javax.inject.Inject

class LoggerRepository @Inject constructor(private val context: Context) {

    fun getLastLogLines(): Single<List<String>> {
        return Single.fromCallable {
            getLastModified(context.filesDir.absolutePath)
                ?.readText(Charset.defaultCharset())
                .orEmpty()
                .split("\n")
        }
    }

    private fun getLastModified(directoryFilePath: String): File? {
        val directory = File(directoryFilePath)
        val files = directory.listFiles(File::isFile)
        var lastModifiedTime = Long.MIN_VALUE
        var chosenFile: File? = null
        if (files != null) {
            for (file in files) {
                if (file.lastModified() > lastModifiedTime) {
                    chosenFile = file
                    lastModifiedTime = file.lastModified()
                }
            }
        }
        return chosenFile
    }
}
