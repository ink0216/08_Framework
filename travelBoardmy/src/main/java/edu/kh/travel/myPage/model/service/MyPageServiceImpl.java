package edu.kh.travel.myPage.model.service;

import org.springframework.stereotype.Service;

import edu.kh.travel.myPage.model.mapper.MyPageMapper;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MyPageServiceImpl implements MyPageService{
	private final MyPageMapper mapper;
}
