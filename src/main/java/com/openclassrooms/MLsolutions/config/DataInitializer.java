package com.openclassrooms.MLsolutions.config;

import com.openclassrooms.MLsolutions.entity.Patient;
import com.openclassrooms.MLsolutions.repository.PatientRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DataInitializer implements CommandLineRunner {

    private final PatientRepository patientRepository;

    public DataInitializer(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @Override
    public void run(String... args)  {
        if(patientRepository.count() > 0) {
            return;
        }

        patientRepository.save(build("Test", "TestNone", LocalDate.of(1966, 12, 31), "F",
                "1 Brookside St", "100-222-3333"));

        patientRepository.save(build("Test", "TestBorderline", LocalDate.of(1945, 6, 24), "M",
                "2 High St", "200-333-4444"));

        patientRepository.save(build("Test", "TestInDanger", LocalDate.of(2004, 6, 18), "M",
                "3 Club Road", "300-444-5555"));

        patientRepository.save(build("Test", "TestEarlyOnset", LocalDate.of(2002, 6, 28), "F",
                "4 Valley Dr", "400-555-6666"));
    }

    private Patient build(String firstName, String lastName, LocalDate birthDate, String gender, String address, String phone) {
        Patient p = new Patient();
        p.setFirstName(firstName);
        p.setLastName(lastName);
        p.setBirthDate(birthDate);
        p.setGender(gender);
        p.setAddress(address);
        p.setPhone(phone);
        return p;
    }
}
