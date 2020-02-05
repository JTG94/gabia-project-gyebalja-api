package com.gabia.gyebalja.service;

import com.gabia.gyebalja.domain.Board;
import com.gabia.gyebalja.dto.board.BoardRequestDto;
import com.gabia.gyebalja.dto.board.BoardResponseDto;
import com.gabia.gyebalja.repository.BoardRepository;
import com.gabia.gyebalja.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@RequiredArgsConstructor
@Service
public class BoardService {

    @PersistenceContext
    EntityManager em;

    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;


    /** 등록 - board 한 건 (게시글 등록) */
    @Transactional
    public Long save(BoardRequestDto boardRequestDto){
        Long boardId = boardRepository.save(boardRequestDto.toEntity()).getId(); // 검토. getId() 적절한지?

        return boardId;
    }

    /** 조회 - board 한 건 (상세페이지) */
    @Transactional
    public BoardResponseDto findById(Long id){
        Board board = boardRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다."));    // 검토. 404 Error?
        BoardResponseDto boardResponseDto = new BoardResponseDto(board);

//        // 게시글에 속한 댓글 조회, boardDto 에 삽입
//        List<Comment> commentList = commentRepository.findByBoardId(id);
//        List<CommentDto> commentDtoList = commentList.stream().map(comment -> new CommentDto(comment)).collect(Collectors.toList());
//        boardDto.changeCommentList(commentDtoList);

        return boardResponseDto;
    }

    /** 수정 - board 한 건 (상세페이지에서) */
    @Transactional
    public Long update(Long id, BoardRequestDto boardRequestDtoDto){
        Board board = boardRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다."));    // 검토. 404 Error?

        // 더티 체킹
        board.changeTitle(boardRequestDtoDto.getTitle());
        board.changeContent(boardRequestDtoDto.getContent());
        em.flush();
        em.clear();

        return id;
    }

    /** 삭제 - board 한 건 (상세페이지에서) */
    @Transactional
    public Long delete(Long id){
        boardRepository.deleteById(id);
        em.flush();
        em.clear();

        return id;
    }
}
