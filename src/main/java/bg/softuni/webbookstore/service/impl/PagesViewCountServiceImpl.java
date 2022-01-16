package bg.softuni.webbookstore.service.impl;

import bg.softuni.webbookstore.service.PagesViewCountService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class PagesViewCountServiceImpl implements PagesViewCountService {

    private Map<String, Integer> pagesViewsCount;

    public PagesViewCountServiceImpl() {
        pagesViewsCount = new HashMap<>();
    }

    @Override
    public void onRequest(String requestURI) {
        pagesViewsCount.put(requestURI, pagesViewsCount.getOrDefault(requestURI, 0) + 1);
    }

    @Override
    public Integer getPageViewsCount(String requestURI) {
        return pagesViewsCount.get(requestURI);
    }
}
