package HorasLaborales.demo.Repositories.Entries;

import HorasLaborales.demo.Entities.Entries.EntryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EntryRepository extends JpaRepository<EntryEntity, Long> {
    Page<EntryEntity> findAll(Pageable pageable);
}
