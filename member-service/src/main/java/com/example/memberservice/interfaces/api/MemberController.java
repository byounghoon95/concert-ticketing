package com.example.memberservice.interfaces.api;

import com.example.memberservice.domain.service.MemberService;
import com.example.memberservice.interfaces.api.dto.MemberChargeRequest;
import com.example.memberservice.interfaces.api.dto.MemberResponse;
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
