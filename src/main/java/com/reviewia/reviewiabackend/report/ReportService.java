package com.reviewia.reviewiabackend.report;

import com.reviewia.reviewiabackend.notification.NotificationMessages;
import com.reviewia.reviewiabackend.notification.NotificationRepository;
import com.reviewia.reviewiabackend.post.PostService;
import com.reviewia.reviewiabackend.registration.RegistrationService;
import com.reviewia.reviewiabackend.review.ReviewService;
import com.reviewia.reviewiabackend.user.User;
import com.reviewia.reviewiabackend.user.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@AllArgsConstructor
public class ReportService {
    private ReportRepository reportRepository;
    private UserService userService;
    private PostService postService;
    private ReviewService reviewService;
    private NotificationRepository notificationRepository;
    private RegistrationService registrationService;

    public void create(String userEmail, Long subjectId, String type, Report report) {
        try {
            if(!userService.checkUser(userEmail)) throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "user doesn't exist");

            if(type.equalsIgnoreCase("p")) {
                if(!postService.checkPost(subjectId)) throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Post doesn't exist");
                report.setReportType(ReportType.POST);
            }
            else if(type.equalsIgnoreCase("r")) {
                if(!reviewService.checkReview(subjectId)) throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Review doesn't exist");
                report.setReportType(ReportType.REVIEW);
            }
            report.setReportedBy(userEmail);
            report.setSubjectId(subjectId);
            reportRepository.save(report);
        }catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Report is empty");
        }
    }

    public void create(String userEmail, String subjectEmail, String type, Report report) {
        try {
            if(!userService.checkUser(userEmail) || !userService.checkUser(subjectEmail)) throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE);

            if(!type.equalsIgnoreCase("u")) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
            }

            report.setReportType(ReportType.USER);
            report.setReportedBy(userEmail);
            report.setSubjectId(userService.getUser(subjectEmail).getId());
            reportRepository.save(report);
        }catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Report is empty");
        }
    }

    public List<Report> getReports(String type) {
        if(type != null) {
            if(type.equalsIgnoreCase("p")) return reportRepository.findAllByReportTypeOrderByProcessed(ReportType.POST);
            else if(type.equalsIgnoreCase("r")) return reportRepository.findAllByReportTypeOrderByProcessed(ReportType.REVIEW);
        }
        return reportRepository.findAllOrderByProcessed();
    }

    public Report getReportById(Long id) {
        return reportRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NO_CONTENT, "Report doesn't exist"));
    }

    public List<Report> getReportsByReportedBy(String email) {
        try {
            return reportRepository.findAllByReportedBy(email);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NO_CONTENT);
        }
    }

    public List<Report> getReportsBySubjectId(Long id) {
        try {
            return reportRepository.findAllBySubjectId(id);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NO_CONTENT);
        }
    }

    public List<Report> getReportsBySubjectId(String email) {
        try {
            Long id = userService.getUser(email).getId();
            return reportRepository.findAllBySubjectId(id);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NO_CONTENT);
        }
    }

    @Transactional
    public Boolean processReport(Long reportId) {
        Report report = reportRepository.findById(reportId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NO_CONTENT, "Report doesn't exist"));
        if(report.isProcessed()) throw new ResponseStatusException(HttpStatus.PRECONDITION_FAILED, "already blocked");
        report.setProcessed(true);
        Long subjectId = report.getSubjectId();
        ReportType reportType = report.getReportType();
        User user;
        int count;

        if(reportType == ReportType.POST) {
            user = userService.getUser(postService.getPostById(subjectId).getEmail());
            count = user.incrementReportCount();
            userService.saveUser(user);

            if(user.isLocked()) return true;

            if(count < 3) {
                user.triggerNotification(notificationRepository, NotificationMessages.USER_WARNING, ReportType.USER, subjectId);
            } else {
                registrationService.userBlock(user.getEmail());
            }

            postService.blockPost(subjectId);
            return true;
        }
        else if(reportType == ReportType.REVIEW) {
            user = userService.getUser(reviewService.getById(subjectId).getEmail());
            count = user.incrementReportCount();
            userService.saveUser(user);

            if(count <= 2) {
                user.triggerNotification(notificationRepository, NotificationMessages.USER_WARNING, ReportType.USER, subjectId);
            } else {
                registrationService.userBlock(user.getEmail());
            }

            reviewService.block(subjectId);
            return true;
        }

        return false;
    }
}
