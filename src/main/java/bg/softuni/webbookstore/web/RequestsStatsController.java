package bg.softuni.webbookstore.web;

import bg.softuni.webbookstore.service.RequestsStatsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RequestsStatsController {

    private final RequestsStatsService requestsStatsService;

    public RequestsStatsController(RequestsStatsService requestsStatsService) {
        this.requestsStatsService = requestsStatsService;
    }

    @GetMapping("/stats/requests")
    public String statistics(Model model) {
        model.addAttribute("stats", requestsStatsService.getRequestsStats());
        return "requests-stats";
    }
}
