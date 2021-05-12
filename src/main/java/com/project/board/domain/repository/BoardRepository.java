package com.project.board.domain.repository;

import com.project.board.domain.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board,Long> {
    //JpaRepository값은 매핑할 entity와 id의 타입.

    List<Board> findByTitleContaining(String keyword);
}
