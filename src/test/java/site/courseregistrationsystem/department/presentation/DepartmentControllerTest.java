package site.courseregistrationsystem.department.presentation;

import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.payload.JsonFieldType;

import jakarta.servlet.http.Cookie;
import site.courseregistrationsystem.RestDocsSupport;
import site.courseregistrationsystem.department.dto.DepartmentDetail;
import site.courseregistrationsystem.department.dto.DepartmentList;

class DepartmentControllerTest extends RestDocsSupport {

	@Test
	@DisplayName("학과 목록 조회 : 성공")
	void fetchAllDepartment() throws Exception {
		// given
		String COOKIE_NAME = "SESSIONID";
		String COOKIE_VALUE = "03166dc4-2c82-4e55-85f5-f47919f367a6";
		Cookie sessionCookie = new Cookie(COOKIE_NAME, COOKIE_VALUE);

		List<DepartmentDetail> departmentDetails =
			List.of(
				new DepartmentDetail(1L, "금속공예디자인학과"),
				new DepartmentDetail(2L, "영문학과"),
				new DepartmentDetail(3L, "체육교육학과"
				));
		given(departmentService.fetchAllDepartment())
			.willReturn(new DepartmentList(departmentDetails));

		// when & then
		mockMvc.perform(get("/departments")
				.cookie(sessionCookie))
			.andDo(print())
			.andExpect(status().isOk())
			.andDo(document("fetch-all-department-success",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				responseFields(
					fieldWithPath("code").type(JsonFieldType.NUMBER).description("코드"),
					fieldWithPath("status").type(JsonFieldType.STRING).description("상태"),
					fieldWithPath("message").type(JsonFieldType.STRING).description("메시지"),
					fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
					fieldWithPath("data.departments").type(JsonFieldType.ARRAY).description("응답 데이터"),
					fieldWithPath("data.departments[].id").type(JsonFieldType.NUMBER).description("고유 아이디"),
					fieldWithPath("data.departments[].name").type(JsonFieldType.STRING).description("학과명")
				)
			));
	}

}
