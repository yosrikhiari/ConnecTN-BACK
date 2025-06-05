package tn.esprit.spring.connectn.Entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleUpdateRequest {
    private RoleOng roleOng;
    private List<Competence> competences;
    private List<Test> tests;
}