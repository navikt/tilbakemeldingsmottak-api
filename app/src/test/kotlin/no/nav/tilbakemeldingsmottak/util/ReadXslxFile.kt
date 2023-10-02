package no.nav.tilbakemeldingsmottak.util

import org.apache.poi.ss.usermodel.WorkbookFactory
import java.io.File
import java.io.FileInputStream

internal class ReadXslxFile {
    fun readExcelFile(filePath: String): Map<Int, List<String>> {
        var inputStream: FileInputStream? = null
        return try {
            val resultMap = mutableMapOf<Int, List<String>>()
            val classLoader = javaClass.classLoader
            val file =
                File(classLoader.getResource(filePath)?.file ?: throw IllegalArgumentException("Resource not found"))
            inputStream = FileInputStream(file)
            val workbook = WorkbookFactory.create(inputStream)
            val sheet = workbook.getSheetAt(0)
            val noOfColumns = sheet.getRow(0).physicalNumberOfCells

            for (row in sheet) {
                val rowNum = row.rowNum
                val cellList = mutableListOf<String>()
                for (i in 0 until noOfColumns) {
                    cellList.add(row.getCell(i)?.toString() ?: "")
                }
                resultMap[rowNum] = cellList
            }
            workbook.close()
            resultMap
        } finally {
            inputStream?.close()
        }
    }
}
