package no.nav.tilbakemeldingsmottak;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

import org.apache.poi.ss.usermodel.*;

public class ReadXslxFile {
    public Map<Integer, List<String>> readExcelFile(String filePath) throws IOException {
        FileInputStream inputStream = null;
        try {
            Map<Integer, List<String>> resultMap = new HashMap<>();
            ClassLoader classLoader = getClass().getClassLoader();
            File file = new File(classLoader.getResource(filePath).getFile());

            inputStream = new FileInputStream(file);
            Workbook workbook = WorkbookFactory.create(inputStream);
            Sheet sheet = workbook.getSheetAt(0);

            int noOfColumns = sheet.getRow(0).getPhysicalNumberOfCells();
            for (Row row : sheet) {
                int rowNum = row.getRowNum();
                List<String> cellList = new ArrayList<>();

                for (int i = 0; i < noOfColumns; i++) {
                    cellList.add(row.getCell(i) == null ? "" : row.getCell(i).toString());
                }

                resultMap.put(rowNum, cellList);
            }

            workbook.close();
            inputStream.close();

            return resultMap;
        } finally {
            if (inputStream != null) inputStream.close();
        }
    }
}



