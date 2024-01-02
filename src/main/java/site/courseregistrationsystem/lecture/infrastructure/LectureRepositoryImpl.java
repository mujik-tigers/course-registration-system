package site.courseregistrationsystem.lecture.infrastructure;

import static site.courseregistrationsystem.department.QDepartment.*;
import static site.courseregistrationsystem.lecture.QLecture.*;
import static site.courseregistrationsystem.professor.QProfessor.*;
import static site.courseregistrationsystem.subject.QSubject.*;

import java.time.Year;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.StringUtils;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import site.courseregistrationsystem.lecture.Lecture;
import site.courseregistrationsystem.lecture.Semester;
import site.courseregistrationsystem.subject.SubjectDivision;

@RequiredArgsConstructor
public class LectureRepositoryImpl implements LectureRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public Page<Lecture> findMatchedLectures(Pageable pageable, Year openingYear, Semester semester,
		SubjectDivision subjectDivision, Long departmentId, String subjectName) {
		List<Lecture> lectures = queryFactory.selectFrom(lecture)
			.join(lecture.subject, subject).fetchJoin()
			.join(lecture.subject.department, department).fetchJoin()
			.join(lecture.professor, professor).fetchJoin()
			.where(
				subjectDivisionEq(subjectDivision),
				departmentEq(departmentId),
				subjectNameContains(subjectName),
				lecture.openingYear.eq(openingYear),
				lecture.semester.eq(semester)
			)
			.orderBy(lecture.id.asc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		JPAQuery<Long> countQuery = queryFactory
			.select(lecture.count())
			.from(lecture)
			.where(
				subjectDivisionEq(subjectDivision),
				departmentEq(departmentId),
				subjectNameContains(subjectName)
			);

		return PageableExecutionUtils.getPage(lectures, pageable, countQuery::fetchOne);
	}

	private BooleanExpression subjectDivisionEq(SubjectDivision subjectDivision) {
		if (subjectDivision == null) {
			return null;
		}

		return lecture.subject.subjectDivision.eq(subjectDivision);
	}

	private BooleanExpression departmentEq(Long departmentId) {
		if (departmentId == null) {
			return null;
		}

		return lecture.subject.department.id.eq(departmentId);
	}

	private BooleanExpression subjectNameContains(String subjectName) {
		if (!StringUtils.hasText(subjectName)) {
			return null;
		}

		return lecture.subject.name.contains(subjectName);
	}

}
