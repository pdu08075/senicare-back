package com.korit.senicare.entity;

import com.korit.senicare.dto.request.tool.PostToolRequestDto;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "tools")
@Table(name = "tools")
public class ToolEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)     // toolNumber는 자동 생성이라 직접 값을 넣지 않고 초기에 null 값을 유지하도록 함
    private Integer toolNumber;
    private String name;
    private String purpose;
    private Integer count;
    
    public ToolEntity(PostToolRequestDto dto) {
        this.name = dto.getName();
        this.purpose = dto.getPurpose();
        this.count = dto.getCount();
    }
}
