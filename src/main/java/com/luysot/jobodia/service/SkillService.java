package com.luysot.jobodia.service;

import com.luysot.jobodia.dto.SkillsDTOs.SkillRequestDto;
import com.luysot.jobodia.dto.SkillsDTOs.SkillResponseDto;
import com.luysot.jobodia.mapper.SkillMapper;
import com.luysot.jobodia.model.Skills;
import com.luysot.jobodia.repository.SkillsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SkillService {

    private final SkillsRepository skillsRepository;
    private final SkillMapper skillMapper;

    public SkillResponseDto addSkill(SkillRequestDto dto){
        Skills skills = new Skills();
        skills.setSkillName(dto.skillName());
        skillsRepository.save(skills);
        return skillMapper.toDto(skills);
    }

    public List<SkillResponseDto> findSkills(){
        return skillsRepository.findAll().stream().map(skillMapper::toDto).toList();
    }

    public SkillResponseDto findSkill(Long id){
        return skillMapper.toDto(skillsRepository.findById(id).orElseThrow(()->new RuntimeException("Skill not found!")));
    }

    public SkillResponseDto updateSkill(Long id,SkillRequestDto dto){
        Skills existingSkill = skillsRepository.findById(id).orElseThrow(()->new RuntimeException("Skill not found!"));

        existingSkill.setSkillName(dto.skillName());

        return skillMapper.toDto(skillsRepository.save(existingSkill));
    }

    public void deleteSkill(Long id){
        skillsRepository.deleteById(id);
    }
}
