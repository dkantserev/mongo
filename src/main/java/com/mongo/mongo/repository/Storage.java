package com.mongo.mongo.repository;

import com.mongo.mongo.model.ModelPoi;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface Storage extends MongoRepository<ModelPoi, Integer> {

    @Query("{\"keyValueMap.?0\":\"?1\"}")
    List<ModelPoi> findBy(String p, String s);


}
