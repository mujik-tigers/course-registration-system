package site.courseregistrationsystem.auth.application;

import static org.assertj.core.api.Assertions.*;

import org.assertj.core.data.Offset;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import site.courseregistrationsystem.IntegrationTestSupport;
import site.courseregistrationsystem.auth.StudentSession;
import site.courseregistrationsystem.exception.ErrorType;
import site.courseregistrationsystem.exception.auth.NonexistenceSessionException;

class SessionManagerTest extends IntegrationTestSupport {

	@Autowired
	private SessionManager sessionManager;

	@Test
	@DisplayName("세션 생성 시 길이 36의 세션 ID, 학생 PK, 3600의 만료 시간을 갖는다")
	void generate() {
		// given
		Long studentPk = 100L;

		// when
		StudentSession session = sessionManager.generate(studentPk);

		// then
		assertThat(session.getStudentPk()).isEqualTo(studentPk);
		assertThat(session.getId()).hasSize(36);
		assertThat(session.getExpiration()).isCloseTo(3600L, Offset.offset(10L));
	}

	@Test
	@DisplayName("세션 ID로 데이터베이스에서 세션을 조회한다")
	void fetch() {
		// given
		Long studentPk = 100L;
		StudentSession session = sessionManager.generate(studentPk);

		// when
		StudentSession fetched = sessionManager.fetch(session.getId());

		// then
		assertThat(fetched.getId()).isEqualTo(session.getId());
		assertThat(fetched.getStudentPk()).isEqualTo(session.getStudentPk());
	}

	@Test
	@DisplayName("세션 ID와 일치하는 세션을 데이터베이스에서 삭제한다")
	void invalidate() {
		// given
		Long studentPk = 100L;
		StudentSession session = sessionManager.generate(studentPk);

		// when
		sessionManager.invalidate(session.getId());

		// then
		assertThatThrownBy(() -> sessionManager.fetch(session.getId()))
			.isInstanceOf(NonexistenceSessionException.class)
			.hasMessage(ErrorType.NONEXISTENT_SESSION.getMessage());
	}

	@Test
	@DisplayName("기존의 세션은 무효화하고 동일한 학생 PK를 갖는 새로운 세션을 생성한다")
	void renew() {
		// given
		Long studentPk = 100L;
		StudentSession session = sessionManager.generate(studentPk);

		// when
		StudentSession renewed = sessionManager.renew(session.getId());

		// then
		assertThat(renewed.getId()).isNotEqualTo(session.getId());
		assertThat(renewed.getStudentPk()).isEqualTo(session.getStudentPk());
		assertThatThrownBy(() -> sessionManager.fetch(session.getId()))
			.isInstanceOf(NonexistenceSessionException.class)
			.hasMessage(ErrorType.NONEXISTENT_SESSION.getMessage());
	}

}
