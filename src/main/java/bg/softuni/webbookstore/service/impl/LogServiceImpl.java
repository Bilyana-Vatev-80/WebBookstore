package bg.softuni.webbookstore.service.impl;

import bg.softuni.webbookstore.model.entity.LogEntity;
import bg.softuni.webbookstore.model.view.LogViewModel;
import bg.softuni.webbookstore.repository.LogRepository;
import bg.softuni.webbookstore.service.LogService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LogServiceImpl implements LogService {

    private final LogRepository logRepository;
    private final ModelMapper modelMapper;

    public LogServiceImpl(LogRepository logRepository, ModelMapper modelMapper) {
        this.logRepository = logRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<LogViewModel> findOrderStatusChangeLogs(Long orderId) {
        return logRepository
                .findAllByOrderIdOrderByChangeTimeDesc(orderId)
                .stream()
                .map(this::getLogViewModel)
                .collect(Collectors.toList());
    }

    private LogViewModel getLogViewModel(LogEntity logEntity) {
        return modelMapper
                .map(logEntity, LogViewModel.class)
                .setChangeTime(logEntity.getChangeTime().atZone(ZoneId.systemDefault()));
    }
}
