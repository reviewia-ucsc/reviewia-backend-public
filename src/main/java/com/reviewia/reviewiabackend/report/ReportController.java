package com.reviewia.reviewiabackend.report;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("api")
public class ReportController {
    private ReportService reportService;

    @PostMapping("/user/report")
    public ResponseEntity<?> create(
            @RequestParam("email") String email,
            @RequestParam(value = "subjectId", required = false) Long subjectId,
            @RequestParam(value = "subjectEmail", required = false) String subjectEmail,
            @RequestParam("type") String type,
            @RequestBody Report report
    ) {
        if(subjectId != null)
            reportService.create(email, subjectId, type, report);
        else if(subjectEmail != null)
            reportService.create(email, subjectEmail, type, report);
        return ResponseEntity.status(201).build();
    }

    @GetMapping("/admin/report")
    public ResponseEntity<List<Report>> getAll(@RequestParam(value = "type", required = false) String type) {
        return ResponseEntity.ok(reportService.getReports(type));
    }

    @GetMapping("/admin/report/{id}")
    public ResponseEntity<Report> getByReportId(@PathVariable("id") Long id) {
        return ResponseEntity.ok(reportService.getReportById(id));
    }

    @GetMapping("/admin/report/user")
    public ResponseEntity<List<Report>> getAllByReportedUser(@RequestParam("email") String email) {
        return ResponseEntity.ok(reportService.getReportsByReportedBy(email));
    }

    @GetMapping("/admin/report/subject")
    public ResponseEntity<List<Report>> getBySubject(
            @RequestParam(value = "id" ,required = false) Long id,
            @RequestParam(value = "email", required = false) String email
    ) {
        if(id != null)
            return ResponseEntity.ok(reportService.getReportsBySubjectId(id));
        else if(email != null)
            return ResponseEntity.ok(reportService.getReportsBySubjectId(email));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/admin/report/process")
    public ResponseEntity<Boolean> processByReportId(@RequestParam("id") Long id) {
        return ResponseEntity.ok(reportService.processReport(id));
    }
}
