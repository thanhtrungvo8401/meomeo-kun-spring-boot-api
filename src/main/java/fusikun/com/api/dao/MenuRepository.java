package fusikun.com.api.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fusikun.com.api.model.Menu;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Long>{
	Optional<Menu> findByName(String name);
}
