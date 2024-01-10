package site.courseregistrationsystem.student.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import site.courseregistrationsystem.exception.student.NonexistenceStudentException;
import site.courseregistrationsystem.student.Student;
import site.courseregistrationsystem.student.dto.StudentInformation;
import site.courseregistrationsystem.student.infrastructure.StudentRepository;
import site.courseregistrationsystem.util.encryption.Aes256Manager;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StudentService {

	private final StudentRepository studentRepository;
	private final Aes256Manager aes256Manager;

	@Transactional
	public StudentInformation fetchStudentInformation(Long studentPk) {
		Student student = studentRepository.findById(studentPk)
			.orElseThrow(NonexistenceStudentException::new);

		return new StudentInformation(
			aes256Manager.decrypt(student.getName()),
			aes256Manager.decrypt(student.getStudentId()),
			student.fetchDepartmentName(),
			student.fetchGradeNumber()
		);
	}

}
