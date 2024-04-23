package edu.kh.travel.main.model.service;

import org.springframework.stereotype.Service;

import edu.kh.travel.main.model.mapper.MainMapper;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MainServiceImpl implements MainService{
	private final MainMapper mapper;
}
