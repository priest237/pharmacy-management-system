package com.pharmacy;

import com.pharmacy.model.Medicine;
import com.pharmacy.repository.MedicineRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb;MODE=MySQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "recaptcha.site-key=test-site-key",
        "recaptcha.secret-key=test-secret-key"
})
class AdminMedicineUpdateIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private MedicineRepository medicineRepository;

    private MockMvc mockMvc;
    private Medicine medicine;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        medicineRepository.deleteAll();

        Medicine sample = new Medicine();
        sample.setName("Paracetamol 500mg");
        sample.setDescription("Pain relief tablets");
        sample.setImageUrl("/images/medicines/paracetamol.jpg");
        sample.setPrice(new BigDecimal("3900.00"));
        sample.setStockQuantity(120);
        sample.setExpiryDate(LocalDate.now().plusMonths(12));
        medicine = medicineRepository.save(sample);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void saveChangesUpdatesMedicinePriceInRepository() throws Exception {
        mockMvc.perform(post("/admin/medicines/{id}", medicine.getId())
                        .with(csrf())
                        .param("name", medicine.getName())
                        .param("description", medicine.getDescription())
                        .param("imageUrl", medicine.getImageUrl())
                        .param("price", "5550.00")
                        .param("stockQuantity", String.valueOf(medicine.getStockQuantity()))
                        .param("expiryDate", medicine.getExpiryDate().toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/medicines"));

        Medicine updatedMedicine = medicineRepository.findById(medicine.getId()).orElseThrow();
        assertThat(updatedMedicine.getPrice()).isEqualByComparingTo("5550.00");
    }
}
