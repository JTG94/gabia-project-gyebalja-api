package com.gabia.gyebalja.rank;

import com.gabia.gyebalja.domain.Category;
import com.gabia.gyebalja.domain.Department;
import com.gabia.gyebalja.domain.Education;
import com.gabia.gyebalja.domain.EducationType;
import com.gabia.gyebalja.domain.GenderType;
import com.gabia.gyebalja.domain.User;
import com.gabia.gyebalja.dto.rank.RankResponseDto;
import com.gabia.gyebalja.repository.CategoryRepository;
import com.gabia.gyebalja.repository.DepartmentRepository;
import com.gabia.gyebalja.repository.EducationRepository;
import com.gabia.gyebalja.repository.TagRepository;
import com.gabia.gyebalja.repository.UserRepository;
import com.gabia.gyebalja.service.RankService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Author : 정태균
 * Part : All
 */

@Transactional
@SpringBootTest(properties = "spring.config.location=classpath:application-test.yml")
public class RankServiceTest {

    @PersistenceContext
    EntityManager em;
    @Autowired
    RankService rankService;
    @Autowired
    EducationRepository educationRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    DepartmentRepository departmentRepository;
    @Autowired
    TagRepository tagRepository;

    @Test
    @DisplayName("부서별 랭크 조회 테스트(Service)")
    public void  getRankByDeptId() throws Exception {
        //given
        String currentYear = Integer.toString(LocalDate.now().getYear());
        String userName = "User1";
        Category category = Category.builder()
                .name("개발자")
                .build();
        categoryRepository.save(category);

        Department department = Department.builder()
                .name("테스트팀")
                .depth(2)
                .parentDepartment(null)
                .build();
        Department savedDept = departmentRepository.save(department);

        User user = User.builder()
                .email("test@gabia.com")
                .name(userName)
                .gender(GenderType.MALE)
                .phone("000-000-0000")
                .tel("111-111-1111")
                .positionId(123L)
                .positionName("팀원")
                .department(department)
                .profileImg("src/img")
                .build();
        userRepository.save(user);

        Education education = Education.builder()
                .title("제목테스트")
                .content("내용테스트")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now())
                .totalHours(3)
                .type(EducationType.ONLINE)
                .place("가비아 4층")
                .category(category)
                .user(user)
                .build();

        //when
        List<RankResponseDto> rankByDeptId = rankService.getRankByDeptId(savedDept.getId());

        //then
        assertThat(rankByDeptId.size()).isEqualTo(1);
        assertThat(rankByDeptId.get(0).getUser().getName()).isEqualTo(userName);
        assertThat(rankByDeptId.get(0).getRank()).isEqualTo(1);
    }
}
