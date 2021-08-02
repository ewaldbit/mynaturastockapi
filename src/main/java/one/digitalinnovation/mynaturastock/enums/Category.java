package one.digitalinnovation.mynaturastock.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Category {

    PERFUMARIA("Perfumes e desodorantes"),
    CABELOS("Shampoos e cremes"),
    PELE("Protetores e cremes"),
    BANHO("Sabonetes"),
    ROSTO("Batons, maquiagem");


    private final String description;
}
