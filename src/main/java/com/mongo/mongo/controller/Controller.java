package com.mongo.mongo.controller;

import com.mongo.mongo.model.ModelPoi;
import com.mongo.mongo.Dto.ModelPoiDto;
import com.mongo.mongo.model.QueryParameterSet;
import com.mongo.mongo.service.Service;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.*;
import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
public class Controller {

    final private Service service;

    public Controller(Service service) {
        this.service = service;
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(description = "Сохранить новый элемент")
    public ModelPoi save(@RequestBody Optional<ModelPoiDto> modelPoi, HttpServletRequest request) {
        log.info(request.getRequestURI() + " " + request.getQueryString() + " " + request.getMethod());
        return service.save(modelPoi);
    }

    @GetMapping("/all")
    @Operation(description = "Получить все элементы таблицы")
    public List<ModelPoi> load(
            HttpServletRequest request) {
        log.info(request.getRequestURI() + " " + request.getQueryString() + " " + request.getMethod());
        return service.load();
    }

    @GetMapping("/byQuery")
    @Operation(description = "Найти элемент по параметрам")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "ОК>."),
            @ApiResponse(responseCode = "400", description = "отсутствуют параметры поиска.")
    })
    public List<ModelPoi> findToParam(@RequestParam Optional<String> findByKey,
                                      @RequestParam Optional<String> findByValue,
                                      HttpServletRequest request) {
        log.info(request.getRequestURI() + " " + request.getQueryString() + " " + request.getMethod());
        return service.search(findByKey, findByValue);
    }

    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    @Operation(description = "Загрузить xlsx файл")
    public void p(@RequestPart MultipartFile file,
                  HttpServletRequest request) throws IOException {
        log.info(request.getRequestURI() + " " + request.getQueryString() + " " + request.getMethod());
        service.uploadXlsx(file);
    }

    @GetMapping("/createBase")
    @Operation(description = "Создать таблицу из загруженного файла.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "База создана>."),
            @ApiResponse(responseCode = "404", description = "отсутствует файл для создания базы."),
    })
    @ResponseStatus(HttpStatus.CREATED)
    public String createBase(
            HttpServletRequest request) throws IOException {
        log.info(request.getRequestURI() + " " + request.getQueryString() + " " + request.getMethod());
        return service.createDb();
    }

    @GetMapping("byId/{id}")
    @Operation(description = "Найти элемент по id.")
    public ModelPoi findById(@PathVariable Optional<String> id,
                             HttpServletRequest request) {
        log.info(request.getRequestURI() + " " + request.getQueryString() + " " + request.getMethod());
        return service.findId(id);
    }

    @GetMapping("/getQuery")
    @Operation(description = "Получить список ключей для поиска.")
    public QueryParameterSet getQuery(
            HttpServletRequest request) {
        log.info(request.getRequestURI() + " " + request.getQueryString() + " " + request.getMethod());
        return service.query();
    }

    @GetMapping("/home")
    @ApiResponse(responseCode = "200")
    public String homePage() {
        return "Home";
    }


}
