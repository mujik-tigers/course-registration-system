package site.courseregistrationsystem.department.presentation;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import site.courseregistrationsystem.department.application.DepartmentService;
import site.courseregistrationsystem.department.dto.DepartmentList;
import site.courseregistrationsystem.util.api.ApiResponse;
import site.courseregistrationsystem.util.api.ResponseMessage;

@RestController
@RequiredArgsConstructor
public class DepartmentController {

	private final DepartmentService departmentService;

	@GetMapping("/departments")
	public ApiResponse<DepartmentList> fetchAllDepartments() {
		return ApiResponse.ok(ResponseMessage.DEPARTMENT_FETCH_SUCCESS.getMessage(), departmentService.fetchAllDepartment());
	}

}
