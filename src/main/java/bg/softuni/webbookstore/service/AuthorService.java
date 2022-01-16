package bg.softuni.webbookstore.service;

import bg.softuni.webbookstore.model.service.AuthorAddServiceModel;
import bg.softuni.webbookstore.model.view.AuthorViewModel;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface AuthorService {

    Long add(AuthorAddServiceModel authorAddServiceModel) throws IOException;

    Optional<AuthorViewModel> findById(Long id);
}
