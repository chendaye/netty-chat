package top.chendaye666.websocket.service;

import org.springframework.web.multipart.MultipartFile;
import top.chendaye666.websocket.common.ServerResponse;

import javax.servlet.http.HttpServletRequest;

/**
 * 文件上传
 */
public interface FileUploadService {

    ServerResponse upload(MultipartFile file, HttpServletRequest request);
}
