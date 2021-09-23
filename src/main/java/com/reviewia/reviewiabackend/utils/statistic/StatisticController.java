package com.reviewia.reviewiabackend.utils.statistic;

import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/api/admin/stats")
public class StatisticController {
    private StatisticService statisticService;

    @GetMapping
    public ResponseEntity<Statistic> get() {
        return ResponseEntity.ok(statisticService.generate());
    }

    @GetMapping("/between")
    public ResponseEntity<Long> processDateTime(@RequestParam("type") String type,
                                                @RequestParam(value = "category", required = false) String category,
                                                @RequestParam(value = "subcategory", required = false) String subcategory,
                                                @RequestParam("start")
                                                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
                                                @RequestParam("end")
                                                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return ResponseEntity.ok(statisticService.between(type, category, subcategory, start, end));
    }

    @GetMapping("/chart")
    public ResponseEntity<Map<LocalDate, Long>> chartData(@RequestParam("type") String type,
                                                          @RequestParam("start")
                                                          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
                                                          @RequestParam("end")
                                                          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return ResponseEntity.ok(statisticService.chart(type, start, end));
    }
}
