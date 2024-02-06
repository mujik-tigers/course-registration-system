package site.courseregistrationsystem.student.presentation;

import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.payload.JsonFieldType;

import jakarta.servlet.http.Cookie;
import site.courseregistrationsystem.RestDocsSupport;
import site.courseregistrationsystem.student.dto.StudentInformation;

class StudentControllerTest extends RestDocsSupport {

	@Test
	@DisplayName("학생 정보 조회 : 성공")
	void fetchStudentInformation() throws Exception {
		// given
		String COOKIE_NAME = "SESSIONID";
		String COOKIE_VALUE = "03166dc4-2c82-4e55-85f5-f47919f367a6";
		Cookie sessionCookie = new Cookie(COOKIE_NAME, COOKIE_VALUE);
		StudentInformation studentInformation = new StudentInformation(
			"황현",
			"201711282",
			"전기전자공학부",
			4
		);

		given(studentService.fetchStudentInformation(any()))
			.willReturn(studentInformation);

		// when & then
		mockMvc.perform(get("/students")
				.cookie(sessionCookie))
			.andDo(print())
			.andExpect(status().isOk())
			.andDo(document("student-information-fetch-success",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				responseFields(
					fieldWithPath("code").type(JsonFieldType.NUMBER).description("코드"),
					fieldWithPath("status").type(JsonFieldType.STRING).description("상태"),
					fieldWithPath("message").type(JsonFieldType.STRING).description("메시지"),
					fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
					fieldWithPath("data.name").type(JsonFieldType.STRING).description("학생 이름"),
					fieldWithPath("data.studentId").type(JsonFieldType.STRING).description("학번"),
					fieldWithPath("data.departmentName").type(JsonFieldType.STRING).description("학과"),
					fieldWithPath("data.grade").type(JsonFieldType.NUMBER).description("학년")
				)
			));
	}

	@Test
	@DisplayName("학생 정보 조회 : 실패")
	void fetchFailStudentInformation() throws Exception {
		// when & then
		mockMvc.perform(get("/students"))
			.andDo(print())
			.andExpect(status().isUnauthorized())
			.andDo(document("student-information-fetch-fail",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				responseFields(
					fieldWithPath("code").type(JsonFieldType.NUMBER).description("코드"),
					fieldWithPath("status").type(JsonFieldType.STRING).description("상태"),
					fieldWithPath("message").type(JsonFieldType.STRING).description("메시지"),
					fieldWithPath("data").type(JsonFieldType.NULL).description("응답 데이터")
				)
			));
	}

}
