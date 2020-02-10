package com.gabia.gyebalja.board;

import com.gabia.gyebalja.domain.Board;
import com.gabia.gyebalja.domain.Category;
import com.gabia.gyebalja.domain.Comment;
import com.gabia.gyebalja.domain.Department;
import com.gabia.gyebalja.domain.Education;
import com.gabia.gyebalja.domain.EducationType;
import com.gabia.gyebalja.domain.GenderType;
import com.gabia.gyebalja.domain.User;
import com.gabia.gyebalja.dto.board.BoardRequestDto;
import com.gabia.gyebalja.dto.board.BoardResponseDto;
import com.gabia.gyebalja.repository.BoardRepository;
import com.gabia.gyebalja.repository.CategoryRepository;
import com.gabia.gyebalja.repository.CommentRepository;
import com.gabia.gyebalja.repository.DepartmentRepository;
import com.gabia.gyebalja.repository.EducationRepository;
import com.gabia.gyebalja.repository.UserRepository;
import com.gabia.gyebalja.service.BoardService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BoardControllerTest {
    @Autowired
    private BoardService boardService;

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    @PersistenceContext
    EntityManager em;

    private final BoardRepository boardRepository;
    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final EducationRepository educationRepository;
    private final CommentRepository commentRepository;

    private Department department;
    private User user;
    private Education education;
    private Category category;

    @BeforeEach
    public void setUp(){
        departmentRepository.save(this.department);
        userRepository.save(this.user);
        categoryRepository.save(this.category);
        educationRepository.save(this.education);
    }

    @AfterEach
    public void cleanUp() {
        System.out.println(">>>>>>>>>>>>>>>>>>>> cleanUp() method");

        this.boardRepository.deleteAll();
        this.departmentRepository.deleteAll();
        this.userRepository.deleteAll();
        this.categoryRepository.deleteAll();
        this.educationRepository.deleteAll();
        this.commentRepository.deleteAll();
    }

    @Autowired
    public BoardControllerTest(BoardRepository boardRepository, DepartmentRepository departmentRepository, UserRepository userRepository, CategoryRepository categoryRepository, EducationRepository educationRepository, CommentRepository commentRepository) {
        System.out.println(">>>>>>>>>>>>>>>>>>>> BoardControllerTest() method");

        // Repository
        this.boardRepository = boardRepository;
        this.departmentRepository = departmentRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.educationRepository = educationRepository;
        this.commentRepository = commentRepository;

        // Department
        this.department = Department.builder()
                .name("테스트팀")
                .depth(0)
                .parentDepartment(null)
                .build();

        // User
        this.user = User.builder()
                .email("gabiaUser@gabia.com")
                .password("1234")
                .name("가비아")
                .gender(GenderType.MALE)
                .phone("010-2345-5678")
                .tel("02-2345-5678")
                .positionId(5L)
                .positionName("직원")
                .department(this.department)
                .profileImg(null)
                .build();

        // Category
        this.category = Category.builder()
                .name("개발")
                .build();

        // Education
        this.education = Education.builder()
                .title("테스트 - Mysql 초급 강좌 제목")
                .content("테스트 - Mysql 초급 강좌 본문")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now())
                .totalHours(10)
                .type(EducationType.ONLINE)
                .place("테스트 - 인프런 온라인 교육 사이트")
                .user(this.user)
                .category(this.category)
                .build();
    }

    /**
     * 등록 - board 한 건 (게시글 등록)
     */
    @Test
    @DisplayName("BoardController.postOneBoard() 테스트 (단건 저장)")
    public void postOneBoard() {
        // given
        String title = "테스트 - BoardRequestDto title";
        String content = "테스트 - BoardRequestDto content";
        String url = "http://localhost:" + port + "/api/v1/boards";

        BoardRequestDto boardRequestDto = BoardRequestDto.builder().title(title).content(content).user(user).education(education).build();

        // when
        ResponseEntity<Long> responseEntity = restTemplate.postForEntity(url, boardRequestDto, Long.class);

        // then
        BoardResponseDto boardResponseDto = boardService.getOneBoard(responseEntity.getBody());
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isGreaterThan(0L);
        assertThat(boardResponseDto.getTitle()).isEqualTo(title);
        assertThat(boardResponseDto.getContent()).isEqualTo(content);
    }

    /**
     * 조회 - board 한 건 (상세페이지)
     */
    @Test
    @DisplayName("BoardController.getOneBoard() 테스트 (단건 조회)")
    public void getOneBoard() {
        // given
        String title = "테스트 - BoardRequestDto title";
        String content = "테스트 - BoardRequestDto content";
        BoardRequestDto boardRequestDto = BoardRequestDto.builder().title(title).content(content).user(user).education(education).build();

        Long saveId = boardService.postOneBoard(boardRequestDto);
        String url = "http://localhost:" + port + "/api/v1/boards/" + saveId;

        // when
        ResponseEntity<BoardResponseDto> responseEntity = restTemplate.getForEntity(url, BoardResponseDto.class);
        System.out.println(responseEntity.getBody());

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody().getTitle()).isEqualTo(title);
        assertThat(responseEntity.getBody().getContent()).isEqualTo(content);
    }

    @Test
    @DisplayName("BoardController.getOneBoard() 테스트 (단건 조회) - 댓글 테스트")
    public void getOneBoardWithComments() {
        // given
        String title = "테스트 - BoardRequestDto title";
        String content = "테스트 - BoardRequestDto content";
        BoardRequestDto boardRequestDto = BoardRequestDto.builder().title(title).content(content).user(user).education(education).build();

        Long saveId = boardService.postOneBoard(boardRequestDto);
        String url = "http://localhost:" + port + "/api/v1/boards/" + saveId;

        int totalNumberOfData = 29;
        Board board = boardRepository.findById(saveId).orElseThrow(() -> new IllegalArgumentException("해당 데이터가 없습니다."));
        for(int i =0; i < totalNumberOfData; i++) {
            commentRepository.save(Comment.builder().content("테스트 - 댓글").user(user).board(board).build());
        }

        // when
        ResponseEntity<BoardResponseDto> responseEntity = restTemplate.getForEntity(url, BoardResponseDto.class);

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody().getTitle()).isEqualTo(title);
        assertThat(responseEntity.getBody().getContent()).isEqualTo(content);
        assertThat(responseEntity.getBody().getCommentList().size()).isEqualTo(totalNumberOfData);
    }

    /**
     * 수정 - board 한 건 (상세페이지에서)
     */
    @Test
    @DisplayName("BoardController.putOneBoard() 테스트 (단건 업데이트)")
    public void putOneBoard() {
        // given
        String title = "테스트 - BoardRequestDto title";
        String content = "테스트 - BoardRequestDto content";
        BoardRequestDto saveBoardRequestDto = BoardRequestDto.builder().title(title).content(content).user(user).education(education).build();
        Long saveId = boardService.postOneBoard(saveBoardRequestDto);

        Long updateId = saveId;
        String updateTitle = "테스트 - BoardRequestDto title 업데이트";
        String updateContent = "테스트 - BoardRequestDto content 업데이트";
        String url = "http://localhost:" + port + "/api/v1/boards/" + updateId;

        BoardRequestDto boardRequestDto = BoardRequestDto.builder().title(updateTitle).content(updateContent).user(user).education(education).build();
        HttpEntity<BoardRequestDto> requestEntity = new HttpEntity<>(boardRequestDto);

        // when
        ResponseEntity<Long> responseEntity = restTemplate.exchange(url, HttpMethod.PUT, requestEntity, Long.class); // restTemplate.put(url, boardDto)

        // then
        Board board = boardRepository.findById(updateId).orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다."));
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isGreaterThan(0L);
        assertThat(board.getTitle()).isEqualTo(updateTitle);
        assertThat(board.getContent()).isEqualTo(updateContent);
    }

    /**
     * 삭제 - board 한 건 (상세페이지에서)
     */
    @Test
    @DisplayName("BoardController.deleteOneBoard() 테스트 (단건 삭제)")
    public void deleteOneBoard() {
        //given
        long totalNumberOfData = boardRepository.count();
        String title = "테스트 - BoardRequestDto title";
        String content = "테스트 - BoardRequestDto content";
        BoardRequestDto saveBoardRequestDto = BoardRequestDto.builder().title(title).content(content).user(user).education(education).build();
        Long saveId = boardService.postOneBoard(saveBoardRequestDto);

        Long deleteId = saveId;
        String url = "http://localhost:" + port + "/api/v1/boards/" + deleteId;

        HttpHeaders headers = new HttpHeaders();
        HttpEntity requestEntity = new HttpEntity(headers);

        //when
        ResponseEntity<Long> responseEntity = restTemplate.exchange(url, HttpMethod.DELETE, requestEntity, Long.class);

        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isGreaterThan(0L);
        assertThat(boardRepository.count()).isEqualTo(totalNumberOfData);
    }

    /**
     * 조회 - board 전체 (페이징)
     */
    @Test
    @DisplayName("BoardController.getAllBoard() 테스트 (전체 조회, 페이징)")
    public void getAllBoard() {
        //given
        int totalNumberOfData = 29;
        String title = "테스트 - BoardRequestDto title";
        String content = "테스트 - BoardRequestDto content";
        for (int i = 0; i < totalNumberOfData; i++) {
            boardService.postOneBoard(BoardRequestDto.builder().title(title).content(content).user(user).education(education).build());
        }

        String url = "http://localhost:" + port + "/api/v1/boards";

        HttpHeaders headers = new HttpHeaders();
        HttpEntity requestEntity = new HttpEntity(headers);

        // when
        ResponseEntity<RestPageImpl> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, RestPageImpl.class);

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody().getTotalElements()).isEqualTo(totalNumberOfData);

//        // 테스트 - 값 비교 (추가 예정)
//        ObjectMapper mapper = new ObjectMapper();
//        BoardResponseDto boardResponseDto = mapper.convertValue(responseEntity.getBody().getContent().get(0), BoardResponseDto.class);
//        assertThat(boardResponseDto.getTitle()).isEqualTo(title);
    }
}
