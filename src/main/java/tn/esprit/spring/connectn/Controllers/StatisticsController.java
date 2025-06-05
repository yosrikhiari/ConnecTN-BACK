package tn.esprit.spring.connectn.Controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tn.esprit.spring.connectn.DTO.IssueDTO.IssueStatisticsDTO;
import tn.esprit.spring.connectn.Services.Interfaces.IssueService;

import java.util.List;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    private final IssueService issueService;

    public StatisticsController(IssueService issueService) {
        this.issueService = issueService;
    }

    @GetMapping("/issues-by-city")
    public List<IssueStatisticsDTO> getIssueStatisticsByCity() {
        return issueService.getIssueStatisticsByCity();
    }
}