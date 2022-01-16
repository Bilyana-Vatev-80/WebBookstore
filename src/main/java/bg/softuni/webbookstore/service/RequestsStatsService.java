package bg.softuni.webbookstore.service;

import bg.softuni.webbookstore.model.view.RequestsStatsView;

public interface RequestsStatsService {

    void onRequest();

    RequestsStatsView getRequestsStats();
}
