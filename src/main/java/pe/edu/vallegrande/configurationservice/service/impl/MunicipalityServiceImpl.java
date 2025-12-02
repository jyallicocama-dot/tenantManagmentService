package pe.edu.vallegrande.configurationservice.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import pe.edu.vallegrande.configurationservice.model.Municipality;
import pe.edu.vallegrande.configurationservice.repository.MunicipalityRepository;
import pe.edu.vallegrande.configurationservice.service.MunicipalityService;

import pe.edu.vallegrande.configurationservice.exception.ValidationException;
import pe.edu.vallegrande.configurationservice.exception.DuplicateKeyException;

import lombok.RequiredArgsConstructor;
import java.time.ZonedDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MunicipalityServiceImpl implements MunicipalityService {
    
    private static final Logger log = LoggerFactory.getLogger(MunicipalityServiceImpl.class);
    private final MunicipalityRepository municipalityRepository;

    @Override
    public Flux<Municipality> findAll() {
        return municipalityRepository.findAll();
    }

    @Override
    public Mono<Municipality> findById(UUID id) {
        return municipalityRepository.findById(id);
    }

    @Override
    public Mono<Municipality> create(Municipality municipality) {
        log.info("Starting municipality creation: {}", municipality);
        
        // Basic validations
        if (municipality.getName() == null || municipality.getName().trim().isEmpty()) {
            return Mono.error(new ValidationException("Name is required"));
        }
        if (municipality.getRuc() == null || municipality.getRuc().trim().isEmpty()) {
            return Mono.error(new ValidationException("Tax ID (RUC) is required"));
        }
        if (municipality.getUbigeoCode() == null || municipality.getUbigeoCode().trim().isEmpty()) {
            return Mono.error(new ValidationException("Ubigeo code is required"));
        }

        // Check for duplicates
        return municipalityRepository.findAll()
            .collectList()
            .flatMap(municipalities -> {
                // Check for duplicate ubigeo code
                boolean ubigeoExists = municipalities.stream()
                    .anyMatch(m -> m.getUbigeoCode().equals(municipality.getUbigeoCode()));
                if (ubigeoExists) {
                    return Mono.error(new DuplicateKeyException(
                        "A municipality with this ubigeo code already exists: " + municipality.getUbigeoCode()));
                }

                // Check for duplicate RUC
                boolean rucExists = municipalities.stream()
                    .anyMatch(m -> m.getRuc().equals(municipality.getRuc()));
                if (rucExists) {
                    return Mono.error(new DuplicateKeyException(
                        "A municipality with this tax ID (RUC) already exists: " + municipality.getRuc()));
                }

                // If no duplicates, proceed with creation
                if (municipality.getId() != null) {
                    log.warn("Municipality already has an assigned ID: {}", municipality.getId());
                }

                // Set default values and timestamps
                municipality.setCreatedAt(ZonedDateTime.now());
                municipality.setUpdatedAt(ZonedDateTime.now());
                municipality.setIsActive(municipality.getIsActive() != null ? municipality.getIsActive() : true);
                municipality.setMunicipalityType(municipality.getMunicipalityType() != null ? municipality.getMunicipalityType() : "DISTRICT");

                return municipalityRepository.save(municipality)
                    .doOnSuccess(saved -> log.info("Municipality created successfully: {}", saved))
                    .doOnError(error -> {
                        String errorMessage = error.getMessage() != null ? error.getMessage() : "Unknown error";
                        if (errorMessage.contains("municipalities_ubigeo_key")) {
                            throw new DuplicateKeyException("A municipality with the provided ubigeo code already exists");
                        } else if (errorMessage.contains("municipalities_ruc_key")) {
                            throw new DuplicateKeyException("A municipality with the provided tax ID (RUC) already exists");
                        }
                        log.error("Error creating municipality: {}", errorMessage);
                    });
            });
    }

    @Override
    public Mono<Municipality> update(UUID id, Municipality municipality) {
        return municipalityRepository.findById(id)
            .switchIfEmpty(Mono.error(new RuntimeException("Municipality not found")))
            .flatMap(existingMunicipality -> {
                log.info("Updating municipality: {} with data: {}", id, municipality);
                
                // Update only the fields that come in the request
                if (municipality.getName() != null && !municipality.getName().isEmpty()) {
                    existingMunicipality.setName(municipality.getName());
                }
                if (municipality.getRuc() != null && !municipality.getRuc().isEmpty()) {
                    existingMunicipality.setRuc(municipality.getRuc());
                }
                if (municipality.getUbigeoCode() != null && !municipality.getUbigeoCode().isEmpty()) {
                    existingMunicipality.setUbigeoCode(municipality.getUbigeoCode());
                }
                if (municipality.getMunicipalityType() != null && !municipality.getMunicipalityType().isEmpty()) {
                    existingMunicipality.setMunicipalityType(municipality.getMunicipalityType());
                }
                if (municipality.getDepartment() != null && !municipality.getDepartment().isEmpty()) {
                    existingMunicipality.setDepartment(municipality.getDepartment());
                }
                if (municipality.getProvince() != null && !municipality.getProvince().isEmpty()) {
                    existingMunicipality.setProvince(municipality.getProvince());
                }
                if (municipality.getDistrict() != null && !municipality.getDistrict().isEmpty()) {
                    existingMunicipality.setDistrict(municipality.getDistrict());
                }
                if (municipality.getAddress() != null && !municipality.getAddress().isEmpty()) {
                    existingMunicipality.setAddress(municipality.getAddress());
                }
                if (municipality.getPhoneNumber() != null) {
                    existingMunicipality.setPhoneNumber(municipality.getPhoneNumber());
                }
                if (municipality.getMobileNumber() != null) {
                    existingMunicipality.setMobileNumber(municipality.getMobileNumber());
                }
                if (municipality.getEmail() != null) {
                    existingMunicipality.setEmail(municipality.getEmail());
                }
                if (municipality.getWebsite() != null) {
                    existingMunicipality.setWebsite(municipality.getWebsite());
                }
                if (municipality.getMayorName() != null) {
                    existingMunicipality.setMayorName(municipality.getMayorName());
                }
                if (municipality.getIsActive() != null) {
                    existingMunicipality.setIsActive(municipality.getIsActive());
                }

                existingMunicipality.setUpdatedAt(ZonedDateTime.now());
                
                log.info("Saving updated municipality: {}", existingMunicipality);
                return municipalityRepository.save(existingMunicipality);
            });
    }

    @Override
    public Mono<Void> delete(UUID id) {
        return municipalityRepository.deleteById(id);
    }

    @Override
    public Flux<Municipality> findByMunicipalityType(String municipalityType) {
        return municipalityRepository.findByMunicipalityType(municipalityType);
    }

    @Override
    public Flux<Municipality> findByDepartment(String department) {
        return municipalityRepository.findByDepartment(department);
    }

    @Override
    public Flux<Municipality> findByProvince(String province) {
        return municipalityRepository.findByProvince(province);
    }
}