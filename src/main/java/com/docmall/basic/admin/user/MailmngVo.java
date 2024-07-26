package com.docmall.basic.admin.user;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter 
@ToString
public class MailmngVo {

/*
mailmng_tbl
m_idx, m_title, m_content, m_gubun, m_send_count, reg_date
pk_mailmng_idx
seq_mailmng_tbl
*/	
	
	// resultMap사용해보겠음.
	private Integer idx;
	private String title; 
	private String content; 
	private String gubun; 
	private int sendcount; 
	private Date regDate;   // register + date 가 합쳐진 단어 regDate 
	
	
	
	
	
	
}
