package edu.kh.project.common.scheduling.service;

import java.util.List;

import org.springframework.stereotype.Service;

import edu.kh.project.common.scheduling.mapper.SchedulingMapper;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor //생성자를 이용한 의존성 주입 DI
public class SchedulingServiceImpl implements SchedulingService{
	private final SchedulingMapper mapper;

	//DB에 저장된 이미지 목록 조회
	@Override
	public List<String> selectImageList() {
		return mapper.selectImageList();
	}
}
