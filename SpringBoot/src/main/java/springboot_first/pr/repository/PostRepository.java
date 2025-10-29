package springboot_first.pr.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import springboot_first.pr.model.Post;



@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
}
