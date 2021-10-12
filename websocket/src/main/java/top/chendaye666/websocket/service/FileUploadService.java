package top.chendaye666.websocket.service;

import top.chendaye666.websocket.model.vo.ResponseJson;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

/**
 * 文件上传
 */
public interface FileUploadService {

    ResponseJson upload(MultipartFile file, HttpServletRequest request);
}
