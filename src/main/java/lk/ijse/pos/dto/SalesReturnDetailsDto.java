package lk.ijse.pos.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class SalesReturnDetailsDto {
    private String returnId;
    private String itemCode;
    private int qty;
    private double discRate;
    private double unitPrice;
    private double amount;
}
