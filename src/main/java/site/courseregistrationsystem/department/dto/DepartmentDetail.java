package site.courseregistrationsystem.department.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import site.courseregistrationsystem.department.Department;

@AllArgsConstructor
@Getter
public class DepartmentDetail {

	private final Long id;
	private final String name;

	public DepartmentDetail(Department department) {
		this.id = department.getId();
		this.name = department.getName();
	}

}
