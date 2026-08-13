package HorasLaborales.demo.Repositories.Brands;

import HorasLaborales.demo.Entities.Brands.BrandEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BrandRepository extends JpaRepository<BrandEntity, Long> {
}
