package com.docmall.basic.kakaopay;

import lombok.ToString;

/**
 * Created by kakaopay
 */
@ToString
public class ReadyResponse {
    private String tid;						//결제 고유 번호
    private String created_at;				//결제준비 요청시간
    private String next_redirect_pc_url;	//요청클라이언트가 pc웹일 경우, 카카오톡으로 결제 요청 메시지(TMS)를 보내기 위한 사용자 정보 입력 화면 Redirect URL


    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }


    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getNext_redirect_pc_url() {
        return next_redirect_pc_url;
    }

    public void setNext_redirect_pc_url(String next_redirect_pc_url) {
        this.next_redirect_pc_url = next_redirect_pc_url;
    }

  
}
