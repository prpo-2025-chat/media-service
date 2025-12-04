package com.prpo.chat.media.service;

import java.io.IOException;
import java.io.InputStream;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;

import com.mongodb.client.gridfs.model.GridFSFile;


@Service
public class GridFsService {

    private final GridFsTemplate gridFsTemplate;

    public GridFsService(GridFsTemplate gridFsTemplate) {
        this.gridFsTemplate = gridFsTemplate;
    }


    public String storeFile(InputStream inputStream, String filename, String contentType) {
        ObjectId fileId = gridFsTemplate.store(inputStream, filename, contentType);
        return fileId.toString();
    }


    public GridFsResource getFile(String fileId) {
        GridFSFile gridFsFile = gridFsTemplate.findOne(
            new Query(Criteria.where("_id").is(new ObjectId(fileId)))
        );

        if (gridFsFile == null) {
            return null;
        }

        return gridFsTemplate.getResource(gridFsFile);
    }


    public void deleteFile(String fileId) {
        gridFsTemplate.delete(
            new Query(Criteria.where("_id").is(new ObjectId(fileId)))
        );
    }
}
