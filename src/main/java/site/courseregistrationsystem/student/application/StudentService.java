package site.courseregistrationsystem.student.application;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import site.courseregistrationsystem.exception.student.NonexistenceStudentException;
import site.courseregistrationsystem.student.Student;
import site.courseregistrationsystem.student.dto.StudentInformation;
import site.courseregistrationsystem.student.infrastructure.StudentRepository;
import site.courseregistrationsystem.util.encryption.Aes256Manager;

@Service
@RequiredArgsConstructor
public class StudentService {

	private final StudentRepository studentRepository;
	private final Aes256Manager aes256Manager;

	public StudentInformation fetchStudentInformation(Long studentPK) {
		Student student = studentRepository.findById(studentPK)
			.orElseThrow(NonexistenceStudentException::new);

		return new StudentInformation(
			aes256Manager.decrypt(student.getName()),
			aes256Manager.decrypt(student.getStudentId()),
			student.fetchDepartmentName(),
			student.fetchGradeNumber()
		);
	}

}
