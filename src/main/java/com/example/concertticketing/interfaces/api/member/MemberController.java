package com.example.concertticketing.interfaces.api.member;

import com.example.concertticketing.interfaces.api.member.dto.MemberChargeRequest;
import com.example.concertticketing.interfaces.api.member.dto.MemberResponse;
import com.example.concertticketing.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/balance/{memberId}")
    public ResponseEntity<MemberResponse> getBalance(@PathVariable("memberId") Long memberId) {
        return ResponseEntity.ok(MemberResponse.of(memberService.getBalance(memberId)));
    }

    @PostMapping("/balance")
    public ResponseEntity<MemberResponse> chargeBalance(@RequestBody MemberChargeRequest request) {
        return ResponseEntity.ok(MemberResponse.of(memberService.chargeBalance(request)));
    }
}
