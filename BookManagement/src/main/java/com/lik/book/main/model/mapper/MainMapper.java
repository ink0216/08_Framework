package com.lik.book.main.model.mapper;

import java.util.List;
import java.util.Map;

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

	/**책 검색
	 * @param search
	 * @return
	 */
	public List<Book> search(String keyword);

	/**가격 수정
	 * @param map
	 * @return
	 */
	public int edit(Map<String, Object> map);

	/**책 삭제
	 * @param bookNo
	 * @return
	 */
	public int delete(int bookNo);

}
