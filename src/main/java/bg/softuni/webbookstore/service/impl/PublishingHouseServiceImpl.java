package bg.softuni.webbookstore.service.impl;

import bg.softuni.webbookstore.model.view.PublishingHouseViewModel;
import bg.softuni.webbookstore.repository.PublishingHouseRepository;
import bg.softuni.webbookstore.service.PublishingHouseService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PublishingHouseServiceImpl implements PublishingHouseService {

    private final PublishingHouseRepository publishingHouseRepository;
    private final ModelMapper modelMapper;

    public PublishingHouseServiceImpl(PublishingHouseRepository publishingHouseRepository, ModelMapper modelMapper) {
        this.publishingHouseRepository = publishingHouseRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<String> findAllPublishingHouseNames() {
        return publishingHouseRepository
                .findAllPublishingHouseNames();
    }

    @Override
    public Optional<PublishingHouseViewModel> findById(Long id) {
        return publishingHouseRepository
                .findById(id)
                .map(publishingHouseEntity -> modelMapper
                        .map(publishingHouseEntity, PublishingHouseViewModel.class));
    }
}
