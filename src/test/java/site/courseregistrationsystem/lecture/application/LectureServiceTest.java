package site.courseregistrationsystem.lecture.application;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.stream.IntStream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import site.courseregistrationsystem.IntegrationTestSupport;
import site.courseregistrationsystem.department.Department;
import site.courseregistrationsystem.department.infrastructure.DepartmentRepository;
import site.courseregistrationsystem.lecture.Lecture;
import site.courseregistrationsystem.lecture.dto.LectureFilterOptions;
import site.courseregistrationsystem.lecture.dto.LectureSchedulePage;
import site.courseregistrationsystem.lecture.infrastructure.LectureRepository;
import site.courseregistrationsystem.professor.Professor;
import site.courseregistrationsystem.professor.infrastructure.ProfessorRepository;
import site.courseregistrationsystem.schedule.DayOfWeek;
import site.courseregistrationsystem.schedule.Period;
import site.courseregistrationsystem.schedule.Schedule;
import site.courseregistrationsystem.schedule.infrastructure.ScheduleRepository;
import site.courseregistrationsystem.student.Grade;
import site.courseregistrationsystem.subject.Subject;
import site.courseregistrationsystem.subject.SubjectDivision;
import site.courseregistrationsystem.subject.infrastructure.SubjectRepository;

class LectureServiceTest extends IntegrationTestSupport {

	@Autowired
	private LectureService lectureService;

	@Autowired
	private LectureRepository lectureRepository;

	@Autowired
	private SubjectRepository subjectRepository;

	@Autowired
	private ScheduleRepository scheduleRepository;

	@Autowired
	private DepartmentRepository departmentRepository;

	@Autowired
	private ProfessorRepository professorRepository;

	@Test
	@DisplayName("금속공예디자인학과의 전공 필수 수업이면서 과목명에 공예가 들어가는 강의를 조회한다")
	void fetchWithOptions() {
		// given
		Department department = departmentRepository.save(new Department("금속공예디자인학과"));
		Professor professor = professorRepository.save(new Professor("남유진"));

		List<Subject> majorRequiredSubjects = subjectRepository.saveAll(
			generateSubjectFixtures(30, SubjectDivision.MR, department, "공예"));
		List<Subject> generalRequiredSubjects = subjectRepository.saveAll(
			generateSubjectFixtures(10, SubjectDivision.GR, department, "역사"));

		List<Lecture> matchedLectureFixtures = lectureRepository.saveAll(
			generateLectureFixtures(majorRequiredSubjects, professor));
		List<Lecture> unmatchedLectureFixtures = lectureRepository.saveAll(
			generateLectureFixtures(generalRequiredSubjects, professor));

		scheduleRepository.saveAll(generateScheduleFixtures(matchedLectureFixtures));
		scheduleRepository.saveAll(generateScheduleFixtures(unmatchedLectureFixtures));

		PageRequest pageRequest = PageRequest.of(0, 20, Sort.Direction.ASC, "id");
		LectureFilterOptions lectureFilterOptions = new LectureFilterOptions(SubjectDivision.MR, department.getId(),
			"공예");

		// when
		LectureSchedulePage lectureSchedulePage = lectureService.fetchLectureSchedule(pageRequest,
			lectureFilterOptions);

		// then
		assertAll(() -> assertThat(lectureSchedulePage.isFirst()).isTrue(),
			() -> assertThat(lectureSchedulePage.isLast()).isFalse(),
			() -> assertThat(lectureSchedulePage.getTotalElements()).isEqualTo(30L),
			() -> assertThat(lectureSchedulePage.getLectures()).hasSize(20),
			() -> assertThat(lectureSchedulePage.getLectures().get(0).getId()).isEqualTo(
				matchedLectureFixtures.get(0).getId()),
			() -> assertThat(lectureSchedulePage.getLectures().get(0).getSubjectDivision()).isEqualTo(
				SubjectDivision.MR.getDescription()),
			() -> assertThat(lectureSchedulePage.getLectures().get(0).getSubjectName()).contains(
				lectureFilterOptions.getSubjectName())
		);

	}

	private List<Lecture> generateLectureFixtures(List<Subject> subjects, Professor professor) {
		return IntStream.rangeClosed(1, subjects.size())
			.mapToObj(number -> new Lecture(100100 + number, Integer.toString(100 + number), 10 + number,
				subjects.get(number - 1), professor))
			.toList();
	}

	private List<Subject> generateSubjectFixtures(int size, SubjectDivision subjectDivision, Department department,
		String subjectName) {
		return IntStream.rangeClosed(1, size)
			.mapToObj(number -> new Subject(department, subjectDivision, Grade.FRESHMAN, subjectName + number, 4, 3))
			.toList();
	}

	private List<Schedule> generateScheduleFixtures(List<Lecture> lectures) {
		return lectures.stream().map(lecture -> new Schedule(lecture, DayOfWeek.MON, Period.SIX, Period.NINE)).toList();
	}

}
