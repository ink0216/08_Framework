package com.kh.test.user.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@ToString
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
public class User {
	private int userNo;
	private String userId;
	private String userName;
	private int userAge;
}
