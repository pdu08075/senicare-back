package com.korit.senicare.common.object;

import java.util.ArrayList;
import java.util.List;

import com.korit.senicare.entity.NurseEntity;

import lombok.Getter;

@Getter
public class Nurse {
    
    private String nurseId;
    private String name;
    private String telNumber;

    private Nurse(NurseEntity nurseEntity) {
        this.nurseId = nurseEntity.getUserId();
        this.name = nurseEntity.getName();
        this.telNumber = nurseEntity.getTelNumber();
    }

    public static List<Nurse> getList(List<NurseEntity> nurseEntities) {

        List<Nurse> nurses = new ArrayList<>();
        for (NurseEntity nurseEntity: nurseEntities) {                      // nurseEntities를 반복문 돌릴 건데 NurseEntity 타입의 nurseEntity 에서 받아와서 하나씩 꺼내옴
            Nurse nurse = new Nurse(nurseEntity);
            nurses.add(nurse);
        }
        return nurses;

    }
}
