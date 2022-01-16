package bg.softuni.webbookstore.service;

import bg.softuni.webbookstore.model.view.PublishingHouseViewModel;

import java.util.List;
import java.util.Optional;

public interface PublishingHouseService {

    List<String> findAllPublishingHouseNames();

    Optional<PublishingHouseViewModel> findById(Long id);
}
