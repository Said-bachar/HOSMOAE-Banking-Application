package com.ensa.hosmoaBank.utilities;

import org.springframework.web.multipart.MultipartFile;

public class FilenameUtils {
	
	//Get extension from file name
     public static String getExtension(MultipartFile file){
    	   return file.getOriginalFilename().split("\\.")[1];
     }
}
