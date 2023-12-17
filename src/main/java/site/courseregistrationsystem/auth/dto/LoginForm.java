package site.courseregistrationsystem.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.courseregistrationsystem.validator.PasswordFormatCheck;
import site.courseregistrationsystem.validator.StudentIdFormatCheck;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class LoginForm {

	@StudentIdFormatCheck
	private String studentId;

	@PasswordFormatCheck
	private String password;

}
