package site.courseregistrationsystem.department.dto;

import java.util.List;

import lombok.Getter;

@Getter
public class DepartmentList {

	private final List<DepartmentDetail> departments;

	public DepartmentList(List<DepartmentDetail> departments) {
		this.departments = departments;
	}

}
