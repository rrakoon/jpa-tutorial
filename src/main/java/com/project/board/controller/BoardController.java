package com.project.board.controller;

import com.project.board.dto.BoardDTO;
import com.project.board.service.BoardService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class BoardController {

    private BoardService boardService;

    public BoardController(BoardService boardService){
        this.boardService = boardService;
    }

    @GetMapping("/")
    public String list(Model model, @RequestParam(value = "page", defaultValue = "1") Integer pageNum){
        List<BoardDTO> boardDTOList =  boardService.getBoardList(pageNum);
        Integer[] pageList = boardService.getPageList(pageNum);

        model.addAttribute("boardList",boardDTOList);
        model.addAttribute("pageList", pageList);
        ;
        return "board/list";
    }

    @GetMapping("/post")
    public String write() {
        return "board/write";
    }

    @PostMapping("/post")
    public String write(BoardDTO boardDTO){
        boardService.savePost(boardDTO);
        return "redirect:/";
    }

    @GetMapping("/post/{no}")
    public String detail(@PathVariable("no") Long id, Model model){
        BoardDTO boardDTO = boardService.getPost(id);

        model.addAttribute("boardDto",boardDTO);

        return "board/detail";
    }

    @GetMapping("/post/edit/{no}")
    public String edit(@PathVariable("no") Long id, Model model){
        BoardDTO boardDTO = boardService.getPost(id);
        model.addAttribute("boardDto",boardDTO);

        return "board/update";
    }

    @PutMapping("/post/edit/{no}")
    public String update(BoardDTO boardDTO){
        boardService.savePost(boardDTO);
        return "redirect:/";
    }

    @DeleteMapping("/post/{no}")
    public String delete(@PathVariable("no") Long id, Model model){
        boardService.deletePost(id);

        return "redirect:/";
    }

    @GetMapping("/board/search")
    public String search(@RequestParam("keyword") String keyword, Model model){
        List<BoardDTO> boardDTOList = boardService.searchPosts(keyword);
        model.addAttribute("boardList",boardDTOList);

        return "board/list";
    }
}
