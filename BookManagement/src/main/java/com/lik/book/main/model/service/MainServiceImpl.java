package com.lik.book.main.model.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.lik.book.main.model.dto.Book;
import com.lik.book.main.model.mapper.MainMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MainServiceImpl implements MainService{
	private final MainMapper mapper;
	//도서 삽입
	public int add(Book book) {
		return mapper.add(book);
	}
	//전체 조회
	@Override
	public List<Book> selectAll() {
		return mapper.selectAll();
	}
}
