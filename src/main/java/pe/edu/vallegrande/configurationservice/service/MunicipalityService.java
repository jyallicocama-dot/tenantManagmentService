package pe.edu.vallegrande.configurationservice.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import pe.edu.vallegrande.configurationservice.model.Municipality;
import java.util.UUID;

public interface MunicipalityService {
    Flux<Municipality> findAll();
    
    Mono<Municipality> findById(UUID id);
    
    Mono<Municipality> create(Municipality municipality);
    
    Mono<Municipality> update(UUID id, Municipality municipality);
    
    Mono<Void> delete(UUID id);
    
    Flux<Municipality> findByMunicipalityType(String municipalityType);
    
    Flux<Municipality> findByDepartment(String department);
    
    Flux<Municipality> findByProvince(String province);
}