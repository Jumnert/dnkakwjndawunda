package com.luysot.jobodia.controller;


import com.luysot.jobodia.dto.SeekerProfileDTOs.SeekerSkillsRequestDto;
import com.luysot.jobodia.dto.SeekerProfileDTOs.SeekerSkillsResponseDto;
import com.luysot.jobodia.dto.SkillsDTOs.SkillRequestDto;
import com.luysot.jobodia.dto.SkillsDTOs.SkillResponseDto;
import com.luysot.jobodia.model.Skills;
import com.luysot.jobodia.service.SkillService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/skills")
public class SkillController {

    private final SkillService skillService;

    @PostMapping
    ResponseEntity<SkillResponseDto> addSkill(@RequestBody SkillRequestDto dto){
        return ResponseEntity.ok(skillService.addSkill(dto));
    }

    @GetMapping
    ResponseEntity<List<SkillResponseDto>> findSkills(){
        return ResponseEntity.ok(skillService.findSkills());
    }

    @GetMapping("/{id}")
    ResponseEntity<SkillResponseDto> findSkill(@PathVariable Long id){
        return ResponseEntity.ok().body(skillService.findSkill(id));
    }

    @DeleteMapping("/{id}")
    ResponseEntity<String> deleteSkill(@PathVariable Long id){
        skillService.deleteSkill(id);
        return ResponseEntity.ok("Skill of id " + id + " is deleted!");
    }


}
