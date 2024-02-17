package site.courseregistrationsystem.department.application;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.stream.IntStream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import site.courseregistrationsystem.IntegrationTestSupport;
import site.courseregistrationsystem.department.Department;
import site.courseregistrationsystem.department.dto.DepartmentDetail;
import site.courseregistrationsystem.department.dto.DepartmentList;
import site.courseregistrationsystem.department.infrastructure.DepartmentRepository;

class DepartmentServiceTest extends IntegrationTestSupport {

	@Autowired
	private DepartmentService departmentService;

	@Autowired
	private DepartmentRepository departmentRepository;

	@Test
	@DisplayName("모든 학과의 ID와 학과명 조회하는 데 성공한다")
	void fetchAllDepartmentsSuccess() {
		// given
		List<Department> departments = generateDepartmentFixtures("개설학과명", 20);
		departmentRepository.saveAll(departments);

		// when
		DepartmentList departmentList = departmentService.fetchAllDepartment();

		// then
		List<DepartmentDetail> departmentDetails = departmentList.getDepartments();

		assertThat(departmentDetails).hasSize(20);

		for (int i = 0; i < departmentDetails.size(); i++) {
			assertThat(departmentDetails.get(i)).isInstanceOf(DepartmentDetail.class);
			assertThat(departmentDetails.get(i).getId()).isEqualTo(i + 1);
			assertThat(departmentDetails.get(i).getName()).contains("개설학과명" + (i + 1));
		}
	}

	private List<Department> generateDepartmentFixtures(String name, int size) {
		return IntStream.rangeClosed(1, size)
			.mapToObj(number -> new Department(name + number))
			.toList();
	}

}
