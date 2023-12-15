package site.courseregistrationsystem.student.application;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import site.courseregistrationsystem.student.Student;
import site.courseregistrationsystem.student.dto.LoginForm;
import site.courseregistrationsystem.student.infrastructure.StudentRepository;

@Service
@RequiredArgsConstructor
public class StudentService {

	private final StudentRepository studentRepository;

	public Student login(LoginForm loginForm) {
		return studentRepository.findByLoginForm(loginForm.getStudentId(), loginForm.getPassword())
			.orElseThrow();
	}

}
