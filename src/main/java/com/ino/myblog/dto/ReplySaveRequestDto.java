package com.ino.myblog.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReplySaveRequestDto {
    private  int userId;
    private int boardId;
    private  String content;
    private Timestamp createDate;
}
