package com.project.board.service;

import com.project.board.domain.entity.Board;
import com.project.board.domain.repository.BoardRepository;
import com.project.board.dto.BoardDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BoardService {

    private BoardRepository boardRepository;
    private static final int Block_Page_Num_Cnt = 10; //블럭에 존재하는 페이지 수
    private static final int Page_Post_Cnt = 10; //한 페이지에 존재하는 게시글 수.

    public BoardService(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    @Transactional //DAO에서 처리한 코드 에서리 자동 rollback
    public Long savePost(BoardDTO boardDTO) {
        return boardRepository.save(boardDTO.toEntity()).getId();
    }

    @Transactional
    public List<BoardDTO> getBoardList(Integer pageNum) {
        //PageRequest.of(limit값, 가저올 양, 정렬방식)
        Page<Board> page = boardRepository
                .findAll(PageRequest
                        .of(pageNum - 1, Page_Post_Cnt, Sort.by(Sort.Direction.DESC, "createdDate")));
//        List<Board> boards = boardRepository.findAll();
        List<Board> boards = page.getContent(); //page객체의 리스트 꺼냄.
        List<BoardDTO> boardDTOList = new ArrayList<>();

        for (Board board : boards) {
            BoardDTO boardDTO = BoardDTO.builder()
                    .id(board.getId())
                    .title(board.getTitle())
                    .content(board.getContent())
                    .writer(board.getWriter())
                    .createdDate(board.getCreatedDate())
                    .build();
            boardDTOList.add(boardDTO);
        }
        return boardDTOList;
    }
    @Transactional
    public Long getBoardCnt(){
        return boardRepository.count();
    }

    public Integer[] getPageList(Integer curPageNum){
        Integer[] pageList = new Integer[Block_Page_Num_Cnt];

        //총 게시글 수
        Double postsTotalCnt = Double.valueOf(this.getBoardCnt());

        //총 게시글 수 기준으로 계산한 마지막 페이지 번호 계산
        Integer totalLastPageNum = (int)(Math.ceil(postsTotalCnt/Page_Post_Cnt));

        //현재 페이지 기준 블럭 마지막 페이지 번호 계산
        Integer blockLastPageNum = (totalLastPageNum > curPageNum+Block_Page_Num_Cnt)
                ? curPageNum+Block_Page_Num_Cnt
                :totalLastPageNum;

        //페이지 시작번호 조정
        curPageNum = (curPageNum<=3)? 1: curPageNum-2;

        //페이지 번호 할당
        for(int val=curPageNum, i = 0; val<=blockLastPageNum; val++, i++){
            pageList[i] = val;
        }

        return pageList;
    }


    @Transactional
    public BoardDTO getPost(Long id) {
        //Optional<T> T type객체 포장 래퍼클래스. 모든타입의 참조변수 저장가능.
        // NullPointerException 예외를 제공 메서드로 회피가능. null로 인해 발생하는 예외 처리가능.
        Optional<Board> boardWrapper = boardRepository.findById(id);
        Board board = boardWrapper.get();

        BoardDTO boardDTO = BoardDTO.builder()
                .id(board.getId())
                .title(board.getTitle())
                .content(board.getContent())
                .writer(board.getWriter())
                .createdDate(board.getCreatedDate())
                .build();

        return boardDTO;
    }

    @Transactional
    public void deletePost(Long id) {
        boardRepository.deleteById(id);
    }

    @Transactional
    public List<BoardDTO> searchPosts(String keyword) {
        List<Board> boards = boardRepository.findByTitleContaining(keyword);
        List<BoardDTO> boardDTOList = new ArrayList<>();

        if (boards.isEmpty()) return boardDTOList;

        for (Board board : boards) {
            boardDTOList.add(this.convertEntityToDTO(board));
        }

        return boardDTOList;
    }

    private BoardDTO convertEntityToDTO(Board board) {
        return BoardDTO.builder()
                .id(board.getId())
                .title(board.getTitle())
                .writer(board.getWriter())
                .content(board.getContent())
                .createdDate(board.getCreatedDate())
                .build();

    }

}
