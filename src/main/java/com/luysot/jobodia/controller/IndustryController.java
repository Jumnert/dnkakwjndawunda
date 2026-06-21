package com.luysot.jobodia.controller;


import com.luysot.jobodia.dto.IndustryDTOs.IndustryRequestDto;
import com.luysot.jobodia.dto.IndustryDTOs.IndustryResponseDto;
import com.luysot.jobodia.service.IndustryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/industries")
public class IndustryController {
    private final IndustryService industryService;

    @GetMapping
    ResponseEntity<Set<IndustryResponseDto>> findIndustries(){
        return ResponseEntity.ok(industryService.findIndustries());
    }

    @GetMapping("/{id}")
    ResponseEntity<IndustryResponseDto> findIndustry(@PathVariable Long id){
        return ResponseEntity.ok(industryService.findIndustry(id));
    }

    @PostMapping
    ResponseEntity<IndustryResponseDto> addIndustry(@RequestBody IndustryRequestDto dto){
        return ResponseEntity.ok(industryService.addIndustry(dto));
    }

    @PutMapping("/{id}")
    ResponseEntity<IndustryResponseDto> updateIndustry(@PathVariable Long id,@RequestBody IndustryRequestDto dto){
        return ResponseEntity.ok(industryService.updateIndustry(id,dto));
    }

    @DeleteMapping("/{id}")
    ResponseEntity<?> deleteIndustry(@PathVariable Long id){
        industryService.deleteIndustry(id);
        return ResponseEntity.ok("Industry deleted!!");
    }
}
