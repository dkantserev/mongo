package com.mongo.mongo.controller;

import com.mongo.mongo.model.ModelPoi;
import com.mongo.mongo.Dto.ModelPoiDto;
import com.mongo.mongo.model.QueryParameterSet;
import com.mongo.mongo.service.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.List;

@RestController
public class Controller {

    final private Service service;

    public Controller(Service service) {
        this.service = service;
    }


    @PostMapping
    public ModelPoi save(@RequestBody ModelPoiDto modelPoi) {//добавить новый элемент в базу
        return service.save(modelPoi);
    }

    @GetMapping
    public List<ModelPoi> load() {//получить полный список элементов
        return service.load();
    }

    @GetMapping("/")// поиск по параметрам ключ/значение
    public List<ModelPoi> findToParam(@RequestParam String p,
                                      @RequestParam String s) {
        return service.search(p,s);
    }

    @PostMapping("/file")//загрузка файла xlsx
    public void p(@RequestPart MultipartFile file) throws IOException {
        service.uploadXlsx(file);
    }

    @GetMapping("/createBase")//команда для создания базы из загруженного документа
    public String createBase() throws IOException {
        return service.createDb();
    }

    @GetMapping("/{id}")//поиск по id
    public ModelPoi findById(@PathVariable String id) {
        return service.findId(id);
    }

    @GetMapping("/getQuery")//получить список возможных параметров запроса
    public QueryParameterSet getQuery(){
      return   service.query();
    }

    @GetMapping("/home")
    public String homePage() {
        return "Home";
    }


}
