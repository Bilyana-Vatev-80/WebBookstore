package bg.softuni.webbookstore.service.impl;

import bg.softuni.webbookstore.model.cloudinary.CloudinaryImage;
import bg.softuni.webbookstore.model.entity.AuthorEntity;
import bg.softuni.webbookstore.model.entity.PictureEntity;
import bg.softuni.webbookstore.model.service.AuthorAddServiceModel;
import bg.softuni.webbookstore.model.view.AuthorViewModel;
import bg.softuni.webbookstore.repository.AuthorRepository;
import bg.softuni.webbookstore.repository.PictureRepository;
import bg.softuni.webbookstore.service.AuthorService;
import bg.softuni.webbookstore.service.CloudinaryService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static bg.softuni.webbookstore.constant.GlobalConstants.*;

@Service
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;
    private final PictureRepository pictureRepository;
    private final CloudinaryService cloudinaryService;
    private final ModelMapper modelMapper;

    public AuthorServiceImpl(AuthorRepository authorRepository, PictureRepository pictureRepository, CloudinaryService cloudinaryService, ModelMapper modelMapper) {
        this.authorRepository = authorRepository;
        this.pictureRepository = pictureRepository;
        this.cloudinaryService = cloudinaryService;
        this.modelMapper = modelMapper;
    }

    @Override
    public Long add(AuthorAddServiceModel authorAddServiceModel) throws IOException {

        MultipartFile img = authorAddServiceModel.getImage();

        AuthorEntity authorEntity = modelMapper
                .map(authorAddServiceModel, AuthorEntity.class)
                .setPicture(getPictureEntity(img));

        authorEntity = authorRepository.save(authorEntity);

        return authorEntity.getId();
    }

    @Override
    public Optional<AuthorViewModel> findById(Long id) {
        return authorRepository
                .findById(id)
                .map(authorEntity -> modelMapper
                        .map(authorEntity, AuthorViewModel.class));
    }

    private PictureEntity getPictureEntity(MultipartFile img) throws IOException {
        if (!"".equals(img.getOriginalFilename())) {
            final CloudinaryImage uploaded = cloudinaryService.uploadImage(img);
            return pictureRepository.save(
                    new PictureEntity()
                            .setUrl(uploaded.getUrl())
                            .setPublicId(uploaded.getPublicId())
            );
        } else {
            return pictureRepository.save(new PictureEntity(DEFAULT_AUTHOR_IMAGE_URL));
        }
    }
}
