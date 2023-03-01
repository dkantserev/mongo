package com.mongo.mongo.controller;

import com.mongo.mongo.model.ModelPoi;
import com.mongo.mongo.Dto.ModelPoiDto;
import com.mongo.mongo.model.QueryParameterSet;
import com.mongo.mongo.service.Service;
import jdk.jfr.ContentType;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
public class Controller {

    final private Service service;

    public Controller(Service service) {
        this.service = service;
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ModelPoi save(@RequestBody Optional<ModelPoiDto> modelPoi) {//добавить новый элемент в базу
        return service.save(modelPoi);
    }

    @GetMapping("/all")
    public List<ModelPoi> load() {//получить полный список элементов
        return service.load();
    }

    @GetMapping("/byQuery")// поиск по параметрам ключ/значение
    public List<ModelPoi> findToParam(@RequestParam Optional<String> findByKey,
                                      @RequestParam Optional<String> findByValue) {
        return service.search(findByKey, findByValue);
    }

    @PostMapping(value = "/upload", consumes = "multipart/form-data")//загрузка файла xlsx
    public void p(@RequestPart MultipartFile file) throws IOException {
        service.uploadXlsx(file);
    }

    @GetMapping("/createBase")
    @ResponseStatus(HttpStatus.CREATED)//команда для создания базы из загруженного документа
    public String createBase() throws IOException {
        return service.createDb();
    }

    @GetMapping("byId/{id}")//поиск по id
    public ModelPoi findById(@PathVariable Optional<String> id) {
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
