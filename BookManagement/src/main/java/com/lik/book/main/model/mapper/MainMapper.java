package com.lik.book.main.model.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.lik.book.main.model.dto.Book;

@Mapper
public interface MainMapper {

	/**도서 삽입
	 * @param book
	 * @return
	 */
	public int add(Book book);

	/**전체 도서 삽입
	 * @return
	 */
	public List<Book> selectAll();

}
