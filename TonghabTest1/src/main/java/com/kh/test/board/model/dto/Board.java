package com.kh.test.board.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Board {
	private int boardNo; //
	private String boardTitle; //
	private String userId; 
	private String boardContent;//
	private String boardReadCount; //
	private String boardDate; //
}
