package bg.softuni.webbookstore.service;

public interface PagesViewCountService {

    void onRequest(String requestURI);

    Integer getPageViewsCount(String requestURI);
}
