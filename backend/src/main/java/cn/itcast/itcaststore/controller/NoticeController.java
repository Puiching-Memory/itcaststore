package cn.itcast.itcaststore.controller;

import cn.itcast.itcaststore.domain.Notice;
import cn.itcast.itcaststore.repository.NoticeRepository;
import cn.itcast.itcaststore.util.ResponseResult;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/notices")
public class NoticeController {

    private final NoticeRepository noticeRepository;
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    public NoticeController(NoticeRepository noticeRepository) {
        this.noticeRepository = noticeRepository;
    }

    @GetMapping
    public ResponseResult<List<Notice>> getNotices() {
        List<Notice> notices = noticeRepository.findAllByOrderByIdDesc();
        return ResponseResult.success(notices);
    }

    @GetMapping("/{id}")
    public ResponseResult<Notice> getNotice(@PathVariable Long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("公告不存在"));
        return ResponseResult.success(notice);
    }

    @PostMapping
    public ResponseResult<Notice> createNotice(@Valid @RequestBody NoticeRequest request) {
        Notice notice = new Notice();
        notice.setTitle(request.getTitle());
        notice.setDetails(request.getDetails());
        notice.setTime(LocalDateTime.now().format(TIME_FORMATTER));
        
        Notice savedNotice = noticeRepository.save(notice);
        return ResponseResult.success(savedNotice);
    }

    @PutMapping("/{id}")
    public ResponseResult<Notice> updateNotice(
            @PathVariable Long id,
            @Valid @RequestBody NoticeRequest request) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("公告不存在"));
        
        notice.setTitle(request.getTitle());
        notice.setDetails(request.getDetails());
        // 更新时间
        notice.setTime(LocalDateTime.now().format(TIME_FORMATTER));
        
        Notice updatedNotice = noticeRepository.save(notice);
        return ResponseResult.success(updatedNotice);
    }

    @DeleteMapping("/{id}")
    public ResponseResult<Void> deleteNotice(@PathVariable Long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("公告不存在"));
        
        noticeRepository.delete(notice);
        return ResponseResult.success(null);
    }

    // 请求DTO类
    public static class NoticeRequest {
        @NotBlank(message = "标题不能为空")
        @Size(max = 100, message = "标题长度不能超过100个字符")
        private String title;

        @NotBlank(message = "内容不能为空")
        @Size(max = 2000, message = "内容长度不能超过2000个字符")
        private String details;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDetails() {
            return details;
        }

        public void setDetails(String details) {
            this.details = details;
        }
    }
}
