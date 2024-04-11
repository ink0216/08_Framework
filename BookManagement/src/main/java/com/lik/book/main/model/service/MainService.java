package com.lik.book.main.model.service;

import java.util.List;
import java.util.Map;

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

	/**책 검색
	 * @param search
	 * @return
	 */
	List<Book> search(String keyword);

	/**가격 수정
	 * @param map
	 * @return
	 */
	int edit(Map<String, Object> map);

	/**책 삭제
	 * @param bookNo
	 * @return
	 */
	int delete(int bookNo);

}
