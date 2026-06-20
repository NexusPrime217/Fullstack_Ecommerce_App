package com.ecommerce.services;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService{

    @Override
    public String uploadImage(String path, MultipartFile file) {
        //File names of th current/original file
        String originalFileName=file.getOriginalFilename();

        //Generate a unique file name
        String randomId= UUID.randomUUID().toString();
        assert originalFileName != null;
        String fileName=randomId.concat(originalFileName.substring(originalFileName.lastIndexOf('.')));
        String filePath=path+ File.separator+fileName;

        //Check if path exist and create
        File folder = new File(path);
        if (!folder.exists())
            folder.mkdir();


        //Upload to server
        try{
            Files.copy(file.getInputStream(), Paths.get(filePath));
        }catch (IOException e){
            System.out.println(e.getMessage());
        }

        return fileName;
    }
}
