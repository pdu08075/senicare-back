package com.korit.senicare.service;

import org.springframework.core.io.Resource;                // 파일 반환할 떄 사용(import 자동완성 주의)
import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    
    String upload(MultipartFile file);

    Resource getFile(String fileName);

}
