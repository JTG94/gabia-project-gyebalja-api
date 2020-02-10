package com.gabia.gyebalja.department;

import com.gabia.gyebalja.common.CommonJsonFormat;
import com.gabia.gyebalja.domain.Department;
import com.gabia.gyebalja.repository.DepartmentRepository;
import com.gabia.gyebalja.service.DepartmentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.LinkedHashMap;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DepartmentControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    @Autowired
    private DepartmentRepository departmentRepository;

    private Department department;

    public DepartmentControllerTest(){
        // Department
        this.department = Department.builder()
                .name("테스트 - 부서")
                .depth(0)
                .parentDepartment(null)
                .build();
    }

    @Test
    @DisplayName("DepartmentController.getOneDepartment() 테스트 (단건 조회)")
    public void getOneDepartment(){
        // given
        Long saveId = departmentRepository.save(department).getId();
        String url = "http://localhost:" + port + "/api/v1/departments/" + saveId;

        // when
        ResponseEntity<CommonJsonFormat> responseEntity = restTemplate.getForEntity(url, CommonJsonFormat.class);

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(((LinkedHashMap) responseEntity.getBody().getResponse()).get("id")).isEqualTo(saveId.intValue());
    }
}
