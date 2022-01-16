package bg.softuni.webbookstore.service;

import bg.softuni.webbookstore.model.view.LogViewModel;

import java.util.List;

public interface LogService {

    List<LogViewModel> findOrderStatusChangeLogs(Long orderId);
}
