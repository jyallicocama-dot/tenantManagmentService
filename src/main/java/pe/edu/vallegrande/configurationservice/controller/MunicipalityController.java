package pe.edu.vallegrande.configurationservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import pe.edu.vallegrande.configurationservice.model.Municipality;
import pe.edu.vallegrande.configurationservice.service.MunicipalityService;

import pe.edu.vallegrande.configurationservice.dto.ValidationResponse;
import java.util.UUID;


@RestController
@RequestMapping("/api/v1/municipalities")
@Validated
public class MunicipalityController {

    private final MunicipalityService municipalityService;

    public MunicipalityController(MunicipalityService municipalityService) {
        this.municipalityService = municipalityService;
    }

    @GetMapping
    public Flux<Municipality> getAll() {
        return municipalityService.findAll();
    }

    @GetMapping("/{id}")
    public Mono<Municipality> getById(@PathVariable UUID id) {
        return municipalityService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Municipality> create(@RequestBody Municipality municipality) {
        return municipalityService.create(municipality);
    }

    @PatchMapping("/{id}")
    public Mono<Municipality> patch(@PathVariable UUID id, @RequestBody Municipality municipality) {
        municipality.setId(id);
        return municipalityService.update(id, municipality);
    }

    @PutMapping("/{id}")
    public Mono<Municipality> update(@PathVariable UUID id, @RequestBody Municipality municipality) {
        municipality.setId(id);
        return municipalityService.update(id, municipality);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable UUID id) {
        return municipalityService.delete(id);
    }

    @GetMapping("/validate/tax-id/{taxId}")
    public Mono<ValidationResponse> validateTaxId(
            @PathVariable String taxId,
            @RequestParam(required = false) UUID excludeId) {
        if (taxId == null || taxId.trim().isEmpty()) {
            return Mono.just(new ValidationResponse(false, "tax_id", "The tax ID is required"));
        }
        if (taxId.length() != 11) {
            return Mono.just(new ValidationResponse(false, "tax_id", "The tax ID must have 11 digits"));
        }
        if (!taxId.matches("\\d+")) {
            return Mono.just(new ValidationResponse(false, "tax_id", "The tax ID must only contain numbers"));
        }
        
        return municipalityService.findAll()
            .any(m -> m.getRuc() != null && 
                     m.getRuc().equals(taxId) && 
                     (excludeId == null || !m.getId().equals(excludeId)))
            .map(exists -> exists 
                ? new ValidationResponse(false, "tax_id", "This tax ID is already registered")
                : new ValidationResponse(true, "tax_id", "Valid tax ID"));
    }

    @GetMapping("/validate/ubigeo-code/{ubigeoCode}")
    public Mono<ValidationResponse> validateUbigeoCode(
            @PathVariable String ubigeoCode,
            @RequestParam(required = false) UUID excludeId) {
        if (ubigeoCode == null || ubigeoCode.trim().isEmpty()) {
            return Mono.just(new ValidationResponse(false, "ubigeo_code", "The ubigeo is required"));
        }
        if (ubigeoCode.length() != 6) {
            return Mono.just(new ValidationResponse(false, "ubigeo_code", "The ubigeo must have 6 digits"));
        }
        if (!ubigeoCode.matches("\\d+")) {
            return Mono.just(new ValidationResponse(false, "ubigeo_code", "The ubigeo must only contain numbers"));
        }
        
        return municipalityService.findAll()
            .any(m -> m.getUbigeoCode() != null && 
                     m.getUbigeoCode().equals(ubigeoCode) && 
                     (excludeId == null || !m.getId().equals(excludeId)))
            .map(exists -> exists 
                ? new ValidationResponse(false, "ubigeo_code", "This ubigeo is already registered")
                : new ValidationResponse(true, "ubigeo_code", "Valid ubigeo"));
    }
}