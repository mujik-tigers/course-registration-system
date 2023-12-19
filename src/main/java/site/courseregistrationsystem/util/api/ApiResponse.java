package site.courseregistrationsystem.util.api;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class ApiResponse<T> {

	private int code;
	private String status;
	private String message;
	private T data;

	public ApiResponse(HttpStatus status, String message, T data) {
		this.code = status.value();
		this.status = status.getReasonPhrase();
		this.message = message;
		this.data = data;
	}

	public static <T> ApiResponse<T> of(HttpStatus status, String message, T data) {
		return new ApiResponse<>(status, message, data);
	}

	public static <T> ApiResponse<T> ok(String message, T data) {
		return new ApiResponse<>(HttpStatus.OK, message, data);
	}

}
