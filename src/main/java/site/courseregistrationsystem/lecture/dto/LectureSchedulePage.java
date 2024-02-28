package site.courseregistrationsystem.lecture.dto;

import java.util.List;

import org.springframework.data.domain.Page;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class LectureSchedulePage {

	private int number;
	private int size;
	private boolean first;
	private boolean last;
	private int totalPages;
	private long totalElements;
	private List<LectureDetail> lectures;

	public LectureSchedulePage(Page<LectureDetail> fetchedResult) {
		number = fetchedResult.getNumber();
		size = fetchedResult.getSize();
		first = fetchedResult.isFirst();
		last = fetchedResult.isLast();
		totalPages = fetchedResult.getTotalPages();
		totalElements = fetchedResult.getTotalElements();
		lectures = fetchedResult.getContent();
	}

}

