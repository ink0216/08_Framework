package edu.kh.travel.member.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Member {
		private int 		memberNo;
	   private String memberEmail;
	   private String memberPw;
	   private String memberNickname;
	   private String memberTel;
	   private String memberAddress;
	   private String profileImg;
	   private String enrollDate;
	   private String memberDelFl;
	   private int authority; 
}
