package pe.edu.vallegrande.configurationservice.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import pe.edu.vallegrande.configurationservice.model.Municipality;
import java.util.UUID;

public interface MunicipalityRepository extends ReactiveCrudRepository<Municipality, UUID> {
    Flux<Municipality> findByMunicipalityType(String municipalityType);
    Flux<Municipality> findByDepartment(String department);
    Flux<Municipality> findByProvince(String province);
}