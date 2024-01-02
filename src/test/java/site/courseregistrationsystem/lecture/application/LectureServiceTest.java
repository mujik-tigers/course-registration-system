package site.courseregistrationsystem.lecture.application;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.Year;
import java.util.List;
import java.util.stream.IntStream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import jakarta.persistence.EntityManager;
import site.courseregistrationsystem.IntegrationTestSupport;
import site.courseregistrationsystem.department.Department;
import site.courseregistrationsystem.lecture.Lecture;
import site.courseregistrationsystem.lecture.Semester;
import site.courseregistrationsystem.lecture.dto.LectureFilterOptions;
import site.courseregistrationsystem.lecture.dto.LectureSchedulePage;
import site.courseregistrationsystem.lecture.infrastructure.LectureRepository;
import site.courseregistrationsystem.professor.Professor;
import site.courseregistrationsystem.schedule.DayOfWeek;
import site.courseregistrationsystem.schedule.Period;
import site.courseregistrationsystem.schedule.Schedule;
import site.courseregistrationsystem.student.Grade;
import site.courseregistrationsystem.subject.Subject;
import site.courseregistrationsystem.subject.SubjectDivision;

class LectureServiceTest extends IntegrationTestSupport {

	@Autowired
	private LectureService lectureService;

	@Autowired
	private LectureRepository lectureRepository;

	@Autowired
	private EntityManager entityManager;

	@Test
	@DisplayName("개강년도와 학기 조건과 함께 강의를 조회한다")
	void fetch() {
		// given
		Department department = saveDepartment("departmentName");
		Professor professor = saveProfessor("professorName");
		Subject subject = createSubject(SubjectDivision.MR, Grade.FRESHMAN, "subjectName", 3, 2);
		Subject savedSubject = saveSubject(subject);
		List<Lecture> lectures = lectureRepository.saveAll(
			generateCopiedLectureFixtures(50, savedSubject, professor, department));
		saveSchedules(generateScheduleFixtures(lectures));

		PageRequest pageRequest = PageRequest.of(0, 20, Sort.Direction.ASC, "id");
		LectureFilterOptions lectureFilterOptions = LectureFilterOptions.builder()
			.openingYear(Year.of(2024))
			.semester(Semester.FIRST)
			.build();

		// when
		LectureSchedulePage lectureSchedulePage = lectureService.fetchLectureSchedule(pageRequest,
			lectureFilterOptions);

		// then
		int firstIndex = 0;

		assertAll(() -> assertThat(lectureSchedulePage.isFirst()).isTrue(),
			() -> assertThat(lectureSchedulePage.isLast()).isFalse(),
			() -> assertThat(lectureSchedulePage.getTotalElements()).isEqualTo(lectures.size()),
			() -> assertThat(lectureSchedulePage.getLectures()).hasSize(pageRequest.getPageSize()),
			() -> assertThat(lectureSchedulePage.getLectures().get(firstIndex).getId()).isEqualTo(
				lectures.get(firstIndex).getId()));
	}

	@Test
	@DisplayName("금속공예디자인학과의 전공 필수 수업이면서 과목명에 공예가 들어가는 강의를 조회한다")
	void fetchWithOptions() {
		// given
		Department department = saveDepartment("금속공예디자인학과");
		Professor professor = saveProfessor("남유진");

		List<Subject> majorRequiredSubjects = saveSubjects(
			generateSubjectFixtures(30, SubjectDivision.MR, "공예"));
		List<Subject> generalRequiredSubjects = saveSubjects(
			generateSubjectFixtures(10, SubjectDivision.GR, "역사"));

		List<Lecture> matchedLectureFixtures = lectureRepository.saveAll(
			generateLectureFixtures(majorRequiredSubjects, professor, department));
		List<Lecture> unmatchedLectureFixtures = lectureRepository.saveAll(
			generateLectureFixtures(generalRequiredSubjects, professor, department));

		saveSchedules(generateScheduleFixtures(matchedLectureFixtures));
		saveSchedules(generateScheduleFixtures(unmatchedLectureFixtures));

		PageRequest pageRequest = PageRequest.of(0, 20, Sort.Direction.ASC, "id");
		LectureFilterOptions lectureFilterOptions = LectureFilterOptions.builder()
			.openingYear(Year.of(2024))
			.semester(Semester.FIRST)
			.subjectDivision(SubjectDivision.MR)
			.departmentId(department.getId())
			.subjectName("공예")
			.build();

		// when
		LectureSchedulePage lectureSchedulePage = lectureService.fetchLectureSchedule(pageRequest,
			lectureFilterOptions);

		// then
		int firstIndex = 0;

		assertAll(() -> assertThat(lectureSchedulePage.isFirst()).isTrue(),
			() -> assertThat(lectureSchedulePage.isLast()).isFalse(),
			() -> assertThat(lectureSchedulePage.getTotalElements()).isEqualTo(matchedLectureFixtures.size()),
			() -> assertThat(lectureSchedulePage.getLectures()).hasSize(pageRequest.getPageSize()),
			() -> assertThat(lectureSchedulePage.getLectures().get(firstIndex).getId()).isEqualTo(
				matchedLectureFixtures.get(firstIndex).getId()),
			() -> assertThat(lectureSchedulePage.getLectures().get(firstIndex).getSubjectDivision()).isEqualTo(
				SubjectDivision.MR.getDescription()),
			() -> assertThat(lectureSchedulePage.getLectures().get(firstIndex).getSubjectName()).contains(
				lectureFilterOptions.getSubjectName()));
	}

	private List<Lecture> generateCopiedLectureFixtures(int size, Subject subjects, Professor professor,
		Department department) {
		return IntStream.rangeClosed(1, size)
			.mapToObj(number -> createLecture(100100 + number, Integer.toString(100 + number), 10 + number, subjects,
				professor, department))
			.toList();
	}

	private List<Lecture> generateLectureFixtures(List<Subject> subjects, Professor professor, Department department) {
		return IntStream.rangeClosed(1, subjects.size())
			.mapToObj(number -> createLecture(100100 + number, Integer.toString(100 + number), 10 + number,
				subjects.get(number - 1), professor, department))
			.toList();
	}

	private List<Subject> generateSubjectFixtures(int size, SubjectDivision subjectDivision, String subjectName) {
		return IntStream.rangeClosed(1, size)
			.mapToObj(number -> createSubject(subjectDivision, Grade.FRESHMAN, subjectName + number, 4, 3))
			.toList();
	}

	private List<Schedule> generateScheduleFixtures(List<Lecture> lectures) {
		return lectures.stream()
			.map(lecture -> Schedule.builder()
				.lecture(lecture)
				.dayOfWeek(DayOfWeek.MON)
				.firstPeriod(Period.SIX)
				.lastPeriod(Period.NINE)
				.build())
			.toList();
	}

	private static Lecture createLecture(Integer lectureNumber, String lectureRoom, Integer totalCapacity,
		Subject subject, Professor professor, Department department) {
		return Lecture.builder()
			.openingYear(Year.of(2024))
			.semester(Semester.FIRST)
			.lectureNumber(lectureNumber)
			.lectureRoom(lectureRoom)
			.totalCapacity(totalCapacity)
			.subject(subject)
			.professor(professor)
			.department(department)
			.build();
	}

	private static Subject createSubject(SubjectDivision subjectDivision, Grade targetGrade, String name,
		Integer hoursPerWeek, Integer credits) {
		return Subject.builder()
			.subjectDivision(subjectDivision)
			.targetGrade(targetGrade)
			.name(name)
			.hoursPerWeek(hoursPerWeek)
			.credits(credits)
			.build();
	}

	private Department saveDepartment(String departmentName) {
		Department department = new Department(departmentName);
		entityManager.persist(department);

		return department;
	}

	private Professor saveProfessor(String professorName) {
		Professor professor = new Professor(professorName);
		entityManager.persist(professor);

		return professor;
	}

	private void saveSchedules(List<Schedule> schedules) {
		schedules.forEach(entityManager::persist);
	}

	private Subject saveSubject(Subject subjects) {
		entityManager.persist(subjects);

		return subjects;
	}

	private List<Subject> saveSubjects(List<Subject> subjects) {
		subjects.forEach(entityManager::persist);

		return subjects;
	}

}
