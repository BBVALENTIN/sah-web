package com.sah.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class minimalStateDTO {
    public List<PiesaDTO> Piese;
    public int culoareCurenta; // de facut enum pentru culoare
}
