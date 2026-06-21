package com.luysot.jobodia.service;

import com.luysot.jobodia.dto.IndustryDTOs.IndustryRequestDto;
import com.luysot.jobodia.dto.IndustryDTOs.IndustryResponseDto;
import com.luysot.jobodia.mapper.IndustryMapper;
import com.luysot.jobodia.model.Industries;
import com.luysot.jobodia.repository.IndustryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class IndustryService {
    private final IndustryRepository industryRepository;
    private final IndustryMapper industryMapper;

    public IndustryResponseDto addIndustry(IndustryRequestDto dto){
        Industries industry = new Industries();
        industry.setIndustryName(dto.industryName());

        return industryMapper.toDto(industryRepository.save(industry));
    }

    public Set<IndustryResponseDto> findIndustries(){
        return new HashSet<>(industryRepository.findAll().stream()
                .map(industryMapper::toDto)
                .toList());
    }

    public IndustryResponseDto findIndustry(Long id){
        return industryMapper.toDto(industryRepository.findById(id).orElseThrow(()->new RuntimeException("Industry not found!!")));
    }

    public IndustryResponseDto updateIndustry(Long id, IndustryRequestDto dto){
        Industries industry = industryRepository.findById(id).orElseThrow(()->new RuntimeException("Industry not found!!"));
        industry.setIndustryName(dto.industryName());

        industryRepository.save(industry);

        return industryMapper.toDto(industry);
    }

    public void deleteIndustry(Long id){
        industryRepository.deleteById(id);
    }
}
