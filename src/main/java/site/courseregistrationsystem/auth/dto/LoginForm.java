package site.courseregistrationsystem.auth.dto;

import lombok.Getter;
import site.courseregistrationsystem.validator.PasswordFormatCheck;
import site.courseregistrationsystem.validator.StudentIdFormatCheck;

@Getter
public class LoginForm {

	@StudentIdFormatCheck
	private String studentId;

	@PasswordFormatCheck
	private String password;

}
