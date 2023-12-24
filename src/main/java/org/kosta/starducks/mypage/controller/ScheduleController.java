package org.kosta.starducks.mypage.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kosta.starducks.hr.entity.Employee;
import org.kosta.starducks.hr.service.EmpService;
import org.kosta.starducks.mypage.dto.ScheduleDTO;
import org.kosta.starducks.mypage.entity.Schedule;
import org.kosta.starducks.mypage.service.ScheduleService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/mypage/schedule")

public class ScheduleController {
    private final ScheduleService scheduleService;
    private final EmpService empService;
    private final HttpServletRequest request;

    /**
     * 로그인을 한 사원의 일정 조회
     * <p>
     * 정보를 던져주는 용도의 GetMapping
     *
     * @param
     * @return
     */
    @GetMapping("/api/show")
    @ResponseBody
    public ResponseEntity<List<Schedule>> showSingleSchedule(Principal principal) {

        // 유저 정보 받아오기
        if (principal == null) {
            // 로그인되지 않은 경우 예외 처리 또는 다른 처리 방식을 선택할 수 있습니다.
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String empIdString = principal.getName();
        log.info("Logged in employee ID: " + empIdString); // 이 부분을 추가
        Long empId = Long.valueOf(empIdString);

        List<Schedule> scheduleList = scheduleService.findByEmployeeEmpId(empId);
        return ResponseEntity.ok(scheduleList);
    }

    /**
     * 화면 조회를 위한 GetMapping
     *
     * @param model
     * @return
     */
    @GetMapping("/show")
    public String showSchedule(Model model, Principal principal, ScheduleDTO scheduleDTO) {

        Long empId = 1L;

        // 유저 정보 받아오기
        if (principal != null) {
            empId = Long.valueOf(principal.getName());
        }

        model.addAttribute("empId", empId);
        return "mypage/schedule/schedule";
    }

    /**
     * 일정 등록하기
     *
     * @param scheduleDTO
     * @return
     */
    @PostMapping("/add")
    public ResponseEntity<?> addSchedule(@RequestBody ScheduleDTO scheduleDTO, Principal principal) {

        try {
            if (principal == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            String empIdString = principal.getName();
            Long empId = Long.parseLong(empIdString);
            log.info("여기는 일정 추가 컨트롤러~!~!~!~!~!  scheduleDTO.getEmpId(): " + scheduleDTO.getEmpId()); // 이 부분을 추가

            Employee employee = empService.getEmpById(empId);

            ModelMapper modelMapper = new ModelMapper();
            Schedule schedule = modelMapper.map(scheduleDTO, Schedule.class);
            schedule.setEmployee(employee);

            Schedule savedSchedule = scheduleService.saveSchedule(schedule);
            ScheduleDTO savedScheduleDTO = modelMapper.map(savedSchedule, ScheduleDTO.class);

            return ResponseEntity.ok(savedScheduleDTO);
        } catch (Exception e) {
            log.error("일정 저장 중 오류 발생 ==> " + e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 새로 등록된 일정 상세 조회
     *
     * @param model
     * @param scheNo
     * @return
     */
    @GetMapping("/detail/{scheNo}")
    public String findScheduleDetail(Model model, @PathVariable("scheNo") Long scheNo) {
        ScheduleDTO detailSchedule = scheduleService.findScheduleDetail(scheNo);
        model.addAttribute("detailSchedule", detailSchedule);

        return "mypage/schedule/scheduleDetail";
    }

    /**
     * 기존에 등록되어 있던 일정 상세조회(DB 데이터 상세조회)
     *
     * @param model
     * @param scheNo
     * @return
     */
    @GetMapping("/detailSche/{scheNo}")
    public String findScheduleDetails(Model model, @PathVariable("scheNo") Long scheNo) {
        ScheduleDTO detailSchedule = scheduleService.findScheduleDetail(scheNo);
        model.addAttribute("detailSchedule", detailSchedule);

        return "mypage/schedule/scheduleDetail";
    }

    @GetMapping("/delete/{scheNo}")
    public String deleteSchedule(@PathVariable("scheNo") Long scheNo, RedirectAttributes rttr) {
        scheduleService.deleteSchedule(scheNo, rttr);
        return "redirect:/mypage/schedule/show";
    }
}