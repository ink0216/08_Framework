package com.lik.book.main.model.service;

import java.util.List;

import com.lik.book.main.model.dto.Book;

public interface MainService {

	/**도서 삽입
	 * @param book
	 * @return result
	 */
	int add(Book book);

	/**전체 조회하기
	 * @return bookList
	 */
	List<Book> selectAll();

}
