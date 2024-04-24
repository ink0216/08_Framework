package edu.kh.travel.member.model.mapper;

import org.apache.ibatis.annotations.Mapper;

import edu.kh.travel.member.model.dto.Member;

@Mapper
public interface MemberMapper {
	public Member login(String memberEmail);
}
