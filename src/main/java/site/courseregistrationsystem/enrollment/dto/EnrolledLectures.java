package site.courseregistrationsystem.enrollment.dto;

import java.util.List;

import lombok.Getter;

@Getter
public class EnrolledLectures {

	private final List<EnrolledLectureDetail> enrolledLectures;

	public EnrolledLectures(List<EnrolledLectureDetail> enrolledLectures) {
		this.enrolledLectures = enrolledLectures;
	}

}
