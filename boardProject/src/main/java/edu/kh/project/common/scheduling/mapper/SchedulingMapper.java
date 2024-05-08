package edu.kh.project.common.scheduling.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SchedulingMapper {

	/**DB에 저장된 이미지 목록 조회
	 * @return
	 */
	List<String> selectImageList();

}
