package site.courseregistrationsystem.department.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import site.courseregistrationsystem.department.dto.DepartmentDetail;
import site.courseregistrationsystem.department.dto.DepartmentList;
import site.courseregistrationsystem.department.infrastructure.DepartmentRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DepartmentService {

	private final DepartmentRepository departmentRepository;

	public DepartmentList fetchAllDepartment() {
		return new DepartmentList(departmentRepository.findAll().stream().map(DepartmentDetail::new).toList());
	}

}
