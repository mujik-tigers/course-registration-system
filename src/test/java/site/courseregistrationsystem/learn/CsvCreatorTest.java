package site.courseregistrationsystem.learn;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;

import site.courseregistrationsystem.IntegrationTestSupport;
import site.courseregistrationsystem.student.Grade;
import site.courseregistrationsystem.util.encryption.Aes256Manager;
import site.courseregistrationsystem.util.encryption.BCryptManager;

public class CsvCreatorTest extends IntegrationTestSupport {

	@Autowired
	Aes256Manager aes256Manager;

	// @Test
	@DisplayName("부서 정보를 담는 csv 파일 생성")
	void createDepartmentCsv() throws Exception {
		File csv = new File("./department.csv");
		List<String> departments = departments();

		try (BufferedWriter bw = new BufferedWriter(new FileWriter(csv))) {
			for (int i = 0; i < departments.size(); i++) {
				String row = (i + 1) + "," + departments.get(i);
				bw.write(row);
				bw.newLine();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// @Test
	@DisplayName("학생 csv 파일 생성")
	void createStudentCsv() throws Exception {
		File csv = new File("./student.csv");

		try (BufferedWriter bw = new BufferedWriter(new FileWriter(csv))) {

			List<String> seniorStudentId = createStudentId(2017, 2000);
			for (int i = 0; i < seniorStudentId.size(); i++) {
				String row =
					(i + 1) + ","
						+ randomDepartmentNumber() + ","
						+ seniorStudentId.get(i) + ","
						+ BCryptManager.encrypt("1234567a!") + ","
						+ aes256Manager.encrypt("황현" + i + 1) + ","
						+ Grade.SENIOR.getGradeNumber();

				bw.write(row);
				bw.newLine();
			}

			List<String> juniorStudentId = createStudentId(2018, 1000);
			for (int i = 0; i < juniorStudentId.size(); i++) {
				String row =
					(i + 1 + 2000) + ","
						+ randomDepartmentNumber() + ","
						+ juniorStudentId.get(i) + ","
						+ BCryptManager.encrypt("1234567a!") + ","
						+ aes256Manager.encrypt("김서연" + i + 1) + ","
						+ Grade.JUNIOR.getGradeNumber();

				bw.write(row);
				bw.newLine();
			}

			List<String> sophomoreStudentId = createStudentId(2019, 1000);
			for (int i = 0; i < sophomoreStudentId.size(); i++) {
				String row =
					(i + 1 + 3000) + ","
						+ randomDepartmentNumber() + ","
						+ sophomoreStudentId.get(i) + ","
						+ BCryptManager.encrypt("1234567a!") + ","
						+ aes256Manager.encrypt("홍길동" + i + 1) + ","
						+ Grade.SOPHOMORE.getGradeNumber();

				bw.write(row);
				bw.newLine();
			}

			List<String> freshmanStudentId = createStudentId(2020, 1000);
			for (int i = 0; i < freshmanStudentId.size(); i++) {
				String row =
					(i + 1 + 4000) + ","
						+ randomDepartmentNumber() + ","
						+ freshmanStudentId.get(i) + ","
						+ BCryptManager.encrypt("1234567a!") + ","
						+ aes256Manager.encrypt("김개똥" + i + 1) + ","
						+ Grade.FRESHMAN.getGradeNumber();

				bw.write(row);
				bw.newLine();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private List<String> departments() {
		return List.of(
			"국어국문학과",
			"영어영문학과",
			"중어중문학과",
			"철학과",
			"사학과",
			"미디어커뮤니케이션학과",
			"문화콘텐츠학과",
			"지리학과",
			"수학과",
			"물리학과",
			"화학과",
			"건축학부",
			"사회환경공학부",
			"산업경영공학부",
			"기계항공공학부",
			"전기전자공학부",
			"화학공학부",
			"컴퓨터공학부",
			"생물공학과",
			"정치외교학과",
			"행정학과",
			"경제학과",
			"국제무역학과",
			"응용통계학과",
			"융합인재학과",
			"글로벌비즈니스학과",
			"경영학과",
			"부동산학과",
			"미래에너지공학과",
			"스마트운행체공학과",
			"화장품공학과",
			"줄기세포재생공학과",
			"의생명공학과",
			"생명과학특성학과",
			"동물자원과학과",
			"식량자원과학과",
			"축산식품생명공학과",
			"식품유통공학과",
			"환경보건과학과",
			"산림조경학과",
			"수의예과",
			"수의학과",
			"커뮤니케이션디자인학과",
			"산업디자인학과",
			"의상디자인학과",
			"리빙디자인학과",
			"현대미술학과",
			"영상영화학과",
			"일어교육과",
			"수학교육과",
			"체육교육과",
			"음악교육과",
			"영어교육과",
			"교육공학과"
		);
	}

	private int randomDepartmentNumber() {
		Random rand = new Random();
		return rand.nextInt(departments().size()) + 1;
	}

	private List<String> createStudentId(int admissionYear, int numberOfStudents) {
		IntStream intstream = new Random().ints(admissionYear * 100000 + 1, admissionYear * 100000 + 99999);

		return intstream
			.limit(numberOfStudents)
			.mapToObj(String::valueOf)
			.map(s -> aes256Manager.encrypt(s))
			.collect(Collectors.toList());
	}

}
