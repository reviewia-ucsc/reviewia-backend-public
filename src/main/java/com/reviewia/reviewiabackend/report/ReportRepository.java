package com.reviewia.reviewiabackend.report;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findAllByReportedBy(String email);
    List<Report> findAllBySubjectId(Long id);

    @Query("select r from Report  r where r.reportType=?1 order by r.isProcessed asc ")
    List<Report> findAllByReportTypeOrderByProcessed(ReportType reportType);

    @Query("select r from Report r order by r.isProcessed asc ")
    List<Report> findAllOrderByProcessed();
}
