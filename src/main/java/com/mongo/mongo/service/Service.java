package com.mongo.mongo.service;


import com.mongo.mongo.Dto.ModelPoiDto;

import com.mongo.mongo.exception.BadQueryException;
import com.mongo.mongo.exception.FileNotUploadedException;
import com.mongo.mongo.exception.ObjectNotFoundException;
import com.mongo.mongo.repository.Storage;
import com.mongo.mongo.formulsConverter.ConverterFromFormulaToDouble;
import com.mongo.mongo.model.ModelPoi;
import com.mongo.mongo.model.QueryParameterSet;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.tomcat.util.http.fileupload.IOUtils;


import org.springframework.web.multipart.MultipartFile;


import java.io.*;

import java.util.*;

@Slf4j
@org.springframework.stereotype.Service
public class Service {

    private final Storage storage;


    public Service(Storage storage) {


        this.storage = storage;

    }

    public ModelPoi save(Optional<ModelPoiDto> modelPoi) {
        if (modelPoi.isEmpty()) {
            throw new BadQueryException("missing new element");
        }
        ModelPoi m = new ModelPoi();
        int i = storage.findAll().size();
        m.setId(i + 1);
        m.setKeyValueMap(modelPoi.get().getKeyValueMap());
        return storage.save(m);
    }

    public List<ModelPoi> load() {
        return storage.findAll();
    }


    public ModelPoi findId(Optional<String> s) {
        if (s.isEmpty()) {
            throw new BadQueryException("missing id");
        }
        if (s.get().isBlank()) {
            throw new BadQueryException("void id");
        }
        return storage.findById(Integer.parseInt(s.get()))
                .orElseThrow(() -> new ObjectNotFoundException("object " +
                        s.get() + " not found"));
    }

    public String createDb() throws IOException {

        File file2 = new File("dataBase.xlsx");
        if (!file2.isFile()) {
            throw new FileNotUploadedException("File dataBase.xlsx missing");
        }
        XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(file2));
        int rowMax = workbook.getSheetAt(0).getLastRowNum();
        int trigger = 1;
        List<ModelPoi> list = new ArrayList<>();

        while (rowMax != trigger) {
            Map<Integer, String> keysPosition = new HashMap<>();
            if (trigger == 1) {
                int endCall = workbook.getSheetAt(0).getRow(0).getLastCellNum();
                for (int e = 0; e < endCall; e++) {
                    String key = workbook.getSheetAt(0).getRow(0).getCell(e).getStringCellValue();
                    String v = null;
                    if (key.contains(".")) {
                        v = key.replace('.', ' ');
                        keysPosition.put(e, v);
                    } else {
                        keysPosition.put(e, key);
                    }
                }
            }
            for (int i = 1; i < rowMax; i++) {
                ModelPoi m = new ModelPoi();
                Map<String, String> map = new HashMap<>();
                int endCall = workbook.getSheetAt(0).getRow(0).getLastCellNum();
                for (int j = 0; j < endCall; j++) {

                    if (workbook.getSheetAt(0).getRow(i).getCell(j) != null && workbook.getSheetAt(0)
                            .getRow(i).getCell(j).getCellType().equals(CellType.FORMULA)) {
                        String formula = workbook.getSheetAt(0).getRow(i).getCell(j).getCellFormula();

                        map.put(keysPosition.get(j), String.format("%s", ConverterFromFormulaToDouble
                                .isFormula(workbook, formula, rowMax, endCall)));
                    } else {
                        map.put(keysPosition.get(j), String.format("%s", workbook.getSheetAt(0)
                                .getRow(i).getCell(j)));
                    }
                }
                m.setKeyValueMap(map);
                m.setId(i);
                list.add(m);
                trigger++;
            }
            QueryParameterSet q = new QueryParameterSet();
            for (String value : keysPosition.values()) {
                q.getQuery().add(value);
            }
            File file = new File("query.csv");
            try (FileWriter fileWriter = new FileWriter(file)) {
                fileWriter.write(q.toString());
            }
        }

        storage.saveAll(list);
        workbook.close();
        File file = new File("dataBase.xlsx");
        file.delete();
        return "Создана база myXlsxDataBase, коллекция modelPoi.";
    }

    public void uploadXlsx(MultipartFile file) throws IOException {
        InputStream inputStream = file.getInputStream();
        File file1 = new File("dataBase.xlsx");
        try (OutputStream outputStream = new FileOutputStream(file1)) {
            IOUtils.copy(inputStream, outputStream);
            inputStream.close();
        } catch (Exception ignored) {

        }
    }


    public List<ModelPoi> search(Optional<String> p, Optional<String> s) {
        if (p.isEmpty() || s.isEmpty()) {

            throw new BadQueryException("missing query");
        }
        if (p.get().isBlank() || s.get().isBlank()) {

            throw new BadQueryException("void query");
        }

        return storage.findBy(p.get(), s.get());
    }

    public QueryParameterSet query() {
        QueryParameterSet queryParameterSet = new QueryParameterSet();
        File file = new File("query.csv");
        String st;
        int start;
        int end;
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            st = bufferedReader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        start = st.indexOf("[") + 1;
        end = st.indexOf("]");
        String[] m = st.substring(start, end).split(",");
        for (String s : m) {
            queryParameterSet.getQuery().add(s);
        }
        return queryParameterSet;
    }
}
