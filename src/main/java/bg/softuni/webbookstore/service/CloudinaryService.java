package bg.softuni.webbookstore.service;

import bg.softuni.webbookstore.model.cloudinary.CloudinaryImage;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface CloudinaryService {

    CloudinaryImage uploadImage(MultipartFile multipartFile) throws IOException;

    void deleteImage(String imageUrl);
}
