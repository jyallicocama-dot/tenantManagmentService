package pe.edu.vallegrande.configurationservice.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MunicipalityDTO {
    private UUID id;
    private String name;
    private String ruc;
    private String ubigeoCode;
    private String municipalityType;
    private String department;
    private String province;
    private String district;
    private String address;
    private String phoneNumber;
    private String mobileNumber;
    private String email;
    private String website;
    private String mayorName;
    private Boolean isActive;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
}