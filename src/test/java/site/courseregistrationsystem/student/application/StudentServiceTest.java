package site.courseregistrationsystem.student.application;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import site.courseregistrationsystem.IntegrationTestSupport;
import site.courseregistrationsystem.department.Department;
import site.courseregistrationsystem.department.infrastructure.DepartmentRepository;
import site.courseregistrationsystem.exception.student.NonexistenceStudentException;
import site.courseregistrationsystem.student.Grade;
import site.courseregistrationsystem.student.Student;
import site.courseregistrationsystem.student.dto.StudentInformation;
import site.courseregistrationsystem.student.infrastructure.StudentRepository;
import site.courseregistrationsystem.util.encryption.Aes256Manager;

class StudentServiceTest extends IntegrationTestSupport {

	@Autowired
	private StudentService studentService;

	@Autowired
	private StudentRepository studentRepository;

	@Autowired
	private DepartmentRepository departmentRepository;

	@Autowired
	private Aes256Manager aes256Manager;

	@Test
	@DisplayName("입력으로 받은 학생 PK와 일치하는 학생의 이름, 학번, 학과, 학년을 담은 학생 정보 DTO 를 반환한다.")
	void fetchStudentInformation() throws Exception {
		// given
		String STUDENT_ID = "201711282";
		String NAME = "황현";
		Grade grade = Grade.SENIOR;
		Department department = departmentRepository.save(new Department("전기전자공학부"));

		Student student = new Student(
			aes256Manager.encrypt(STUDENT_ID),
			aes256Manager.encrypt(NAME),
			grade,
			department
		);

		Student savedStudent = studentRepository.save(student);

		// when
		StudentInformation studentInformation = studentService.fetchStudentInformation(savedStudent.getId());

		// then
		assertThat(studentInformation.getStudentId()).isEqualTo(STUDENT_ID);
		assertThat(studentInformation.getName()).isEqualTo(NAME);
		assertThat(studentInformation.getGrade()).isEqualTo(grade.getGradeNumber());
		assertThat(studentInformation.getDepartmentName()).isEqualTo(department.getName());
	}

	@Test
	@DisplayName("입력으로 받은 학생 PK와 일치하는 학생이 존재하지 않는 경우 예외를 발생시킨다.")
	void invalidStudentPK() throws Exception {
		// given
		Long INVALID_STUDENT_PK = 1L;

		// when & then
		assertThatThrownBy(() -> studentService.fetchStudentInformation(INVALID_STUDENT_PK))
			.isInstanceOf(NonexistenceStudentException.class);
	}

}
