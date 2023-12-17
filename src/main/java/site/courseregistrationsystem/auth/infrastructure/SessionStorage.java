package site.courseregistrationsystem.auth.infrastructure;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import site.courseregistrationsystem.auth.StudentSession;

@Repository
public interface SessionStorage extends CrudRepository<StudentSession, String> {

}
