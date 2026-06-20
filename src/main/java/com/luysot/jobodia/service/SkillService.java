package com.luysot.jobodia.service;

import com.luysot.jobodia.dto.SkillsDTOs.SkillRequestDto;
import com.luysot.jobodia.dto.SkillsDTOs.SkillResponseDto;
import com.luysot.jobodia.mapper.SkillMapper;
import com.luysot.jobodia.model.Skills;
import com.luysot.jobodia.repository.SkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SkillService {

    private final SkillRepository skillRepository;
    private final SkillMapper skillMapper;

    public SkillResponseDto addSkill(SkillRequestDto dto){
        Skills skills = new Skills();
        skills.setSkillName(dto.skillName());
        skillRepository.save(skills);
        return skillMapper.toDto(skills);
    }

    public List<SkillResponseDto> findSkills(){
        return skillRepository.findAll().stream().map(skillMapper::toDto).toList();
    }

    public SkillResponseDto findSkill(Long id){
        return skillMapper.toDto(skillRepository.findById(id).orElseThrow(()->new RuntimeException("Skill not found!")));
    }

    public SkillResponseDto updateSkill(Long id,SkillRequestDto dto){
        Skills existingSkill = skillRepository.findById(id).orElseThrow(()->new RuntimeException("Skill not found!"));

        existingSkill.setSkillName(dto.skillName());

        return skillMapper.toDto(skillRepository.save(existingSkill));
    }

    public void deleteSkill(Long id){
        skillRepository.deleteById(id);
    }
}
