package site.courseregistrationsystem.auth.presentation;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import site.courseregistrationsystem.RestDocsSupport;
import site.courseregistrationsystem.auth.StudentSession;
import site.courseregistrationsystem.auth.dto.LoginForm;
import site.courseregistrationsystem.exception.auth.InvalidPasswordException;
import site.courseregistrationsystem.exception.auth.NonexistenceStudentIdException;

class AuthControllerTest extends RestDocsSupport {

	@Test
	@DisplayName("로그인 : 성공")
	void loginSuccess() throws Exception {
		// given
		String studentId = "123456789";
		String password = "test1234!";
		LoginForm loginForm = new LoginForm(studentId, password);

		String uuid = "a8b0a12b-998c-4d9a-a294-e54a95a9b6ba";
		Long studentPk = 1L;
		Long expiration = 3600L;
		given(authService.login(any(LoginForm.class)))
			.willReturn(new StudentSession(uuid, studentPk, expiration));

		// when & then
		String cookieName = cookieProperties.getName();

		mockMvc.perform(post("/login")
				.content(objectMapper.writeValueAsString(loginForm))
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isCreated())
			.andExpect(cookie().exists(cookieName))
			.andExpect(cookie().httpOnly(cookieName, true))
			.andExpect(cookie().secure(cookieName, true))
			.andExpect(cookie().domain(cookieName, cookieProperties.getDomain()))
			.andExpect(cookie().path(cookieName, cookieProperties.getPath()))
			.andExpect(cookie().maxAge(cookieName, cookieProperties.getExpiry()))
			.andDo(document("login-success",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestFields(
					fieldWithPath("studentId").type(JsonFieldType.STRING).description("학번"),
					fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호")
				),
				responseFields(
					fieldWithPath("code").type(JsonFieldType.NUMBER).description("코드"),
					fieldWithPath("status").type(JsonFieldType.STRING).description("상태"),
					fieldWithPath("message").type(JsonFieldType.STRING).description("메시지"),
					fieldWithPath("data").type(JsonFieldType.NULL).description("응답 데이터")
				)
			));
	}

	@Test
	@DisplayName("로그인 : 실패 - 존재하지 않는 학번")
	void loginFailStudentId() throws Exception {
		// given
		String studentId = "012345678";
		String password = "test1234!";
		LoginForm loginForm = new LoginForm(studentId, password);

		given(authService.login(any(LoginForm.class)))
			.willThrow(new NonexistenceStudentIdException());

		// when & then
		mockMvc.perform(post("/login")
				.content(objectMapper.writeValueAsString(loginForm))
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andDo(document("login-fail-student-id",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestFields(
					fieldWithPath("studentId").type(JsonFieldType.STRING).description("학번"),
					fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호")
				),
				responseFields(
					fieldWithPath("code").type(JsonFieldType.NUMBER).description("코드"),
					fieldWithPath("status").type(JsonFieldType.STRING).description("상태"),
					fieldWithPath("message").type(JsonFieldType.STRING).description("메시지"),
					fieldWithPath("data").type(JsonFieldType.NULL).description("응답 데이터")
				)
			));
	}

	@Test
	@DisplayName("로그인 : 실패 - 비밀번호 불일치")
	void loginFailPassword() throws Exception {
		// given
		String studentId = "123456789";
		String password = "test0123!";
		LoginForm loginForm = new LoginForm(studentId, password);

		given(authService.login(any(LoginForm.class)))
			.willThrow(new InvalidPasswordException());

		// when & then
		mockMvc.perform(post("/login")
				.content(objectMapper.writeValueAsString(loginForm))
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isUnauthorized())
			.andDo(document("login-fail-password",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestFields(
					fieldWithPath("studentId").type(JsonFieldType.STRING).description("학번"),
					fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호")
				),
				responseFields(
					fieldWithPath("code").type(JsonFieldType.NUMBER).description("코드"),
					fieldWithPath("status").type(JsonFieldType.STRING).description("상태"),
					fieldWithPath("message").type(JsonFieldType.STRING).description("메시지"),
					fieldWithPath("data").type(JsonFieldType.NULL).description("응답 데이터")
				)
			));
	}

	@Test
	@DisplayName("로그인 : 형식 오류")
	void loginError() throws Exception {
		// given
		String studentId = "12345678";
		String password = "test1234";
		LoginForm loginForm = new LoginForm(studentId, password);

		// when & then
		mockMvc.perform(post("/login")
				.content(objectMapper.writeValueAsString(loginForm))
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andDo(document("login-error",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestFields(
					fieldWithPath("studentId").type(JsonFieldType.STRING).description("학번"),
					fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호")
				),
				responseFields(
					fieldWithPath("code").type(JsonFieldType.NUMBER).description("코드"),
					fieldWithPath("status").type(JsonFieldType.STRING).description("상태"),
					fieldWithPath("message").type(JsonFieldType.NULL).description("메시지"),
					fieldWithPath("data").type(JsonFieldType.ARRAY).description("응답 데이터"),
					fieldWithPath("data[].field").type(JsonFieldType.STRING).description("필드"),
					fieldWithPath("data[].message").type(JsonFieldType.STRING).description("오류 메시지")
				)
			));
	}

	@Test
	@DisplayName("로그인 : 빈 요청 오류")
	void loginNull() throws Exception {
		// given
		LoginForm loginForm = new LoginForm(null, null);

		// when & then
		mockMvc.perform(post("/login")
				.content(objectMapper.writeValueAsString(loginForm))
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andDo(document("login-null",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestFields(
					fieldWithPath("studentId").type(JsonFieldType.NULL).description("학번"),
					fieldWithPath("password").type(JsonFieldType.NULL).description("비밀번호")
				),
				responseFields(
					fieldWithPath("code").type(JsonFieldType.NUMBER).description("코드"),
					fieldWithPath("status").type(JsonFieldType.STRING).description("상태"),
					fieldWithPath("message").type(JsonFieldType.NULL).description("메시지"),
					fieldWithPath("data").type(JsonFieldType.ARRAY).description("응답 데이터"),
					fieldWithPath("data[].field").type(JsonFieldType.STRING).description("필드"),
					fieldWithPath("data[].message").type(JsonFieldType.STRING).description("오류 메시지")
				)
			));
	}

}
