package com.app.util

import java.io.BufferedReader
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStreamReader

object JsonReader
{
    @Throws(IOException::class)
    fun readJSONFile(fileName : String): String
    {
        val assetBasePath = "src/test-common/assets/"
        val br = BufferedReader(InputStreamReader(FileInputStream(assetBasePath + fileName)))
        val sb = StringBuilder()
        var line: String? = br.readLine()
        while (line != null)
        {
            sb.append(line)
            line = br.readLine()
        }

        return sb.toString()
    }
}
