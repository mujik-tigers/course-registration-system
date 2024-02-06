package site.courseregistrationsystem.auth.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.courseregistrationsystem.validator.PasswordFormatCheck;
import site.courseregistrationsystem.validator.StudentIdFormatCheck;

@NoArgsConstructor
@Getter
public class LoginForm {

	@StudentIdFormatCheck
	private String studentId;

	@PasswordFormatCheck
	private String password;

	@Builder
	private LoginForm(String studentId, String password) {
		this.studentId = studentId;
		this.password = password;
	}

}
