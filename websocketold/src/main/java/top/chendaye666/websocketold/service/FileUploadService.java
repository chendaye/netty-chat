package top.chendaye666.websocketold.service;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.multipart.MultipartFile;

import top.chendaye666.websocketold.model.vo.ResponseJson;

public interface FileUploadService {

    ResponseJson upload(MultipartFile file, HttpServletRequest request);
}
