package site.courseregistrationsystem.lecture.dto;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;

import lombok.Getter;

@Getter
public class LectureDetailPage {

	private final int number;
	private final int size;
	private final boolean first;
	private final boolean last;
	private final int totalPages;
	private final long totalElements;
	private final List<LectureDetail> lectures;

	public LectureDetailPage(Page<LectureDetail> fetchedResult) {
		number = fetchedResult.getNumber();
		size = fetchedResult.getSize();
		first = fetchedResult.isFirst();
		last = fetchedResult.isLast();
		totalPages = fetchedResult.getTotalPages();
		totalElements = fetchedResult.getTotalElements();
		lectures = fetchedResult.get().collect(Collectors.toList());
	}

}
